package com.example.paymentservice.payment.adapter.out.persistent.repository;

import com.example.paymentservice.payment.adapter.out.persistent.exception.PaymentAlreadyProcessedException;
import com.example.paymentservice.payment.application.port.out.PaymentStatusUpdateCommand;
import com.example.paymentservice.payment.domain.PaymentStatus;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.data.util.Pair;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.publisher.SynchronousSink;

@Repository
@RequiredArgsConstructor
public class R2DBCPaymentStatusUpdateRepository implements PaymentStatusUpdateRepository {

  private final DatabaseClient databaseClient;
  private final TransactionalOperator transactionalOperator;

  @Override
  public Mono<Boolean> updatePaymentStatusToExecuting(String orderId, String paymentKey) {
    // 1. 현재 결제상태 조회 (결제가 이미 처리되었는데 진행중인 상태로 변경되면 안되니깐)
    return checkPreviousPaymentOrderStatus(orderId)
        .flatMap(pair -> insertPaymentHistory(pair, PaymentStatus.EXECUTING, "PAYMENT_CONFIRMATION_START"))
        .flatMap(result -> updatePaymentOrderStatus(orderId, PaymentStatus.EXECUTING))
        .flatMap(result -> updatePaymentKey(orderId, paymentKey))
        .as(transactionalOperator::transactional)
        .thenReturn(true);
  }

  @Override
  public Mono<Boolean> updatePaymentStatus(PaymentStatusUpdateCommand command) {
    return switch (command.getStatus()) {
      case SUCCESS -> updatePaymentStatusToSuccess(command);
      case FAILURE -> updatePaymentStatusToFailure(command);
      case UNKNOWN -> updatePaymentStatusToUnknown(command);
      default -> throw new IllegalArgumentException(
          "결제 상태 (status: " + command.getStatus() + ") 는 올바르지 않은 결제 상태입니다."
      );
    };
  }

  private Mono<Boolean> updatePaymentStatusToSuccess(PaymentStatusUpdateCommand command) {
    return selectPaymentOrderStatus(command.getOrderId())
        .collectList()
        .flatMap(list -> insertPaymentHistory(list, command.getStatus(), "PAYMENT_CONFIRMATION_DONE"))
        .flatMap(v -> updatePaymentOrderStatus(command.getOrderId(), command.getStatus()))
        .flatMap(v -> updatePaymentEventExtraDetails(command))
//        .flatMap(v -> paymentOutboxRepository.insertOutbox(command))
//        .flatMap(v -> paymentEventMessagePublisher.publishEvent(v))
        .as(transactionalOperator::transactional) // Java에서는 함수형으로 변환
        .thenReturn(true);
  }

  private Mono<Boolean> updatePaymentStatusToFailure(PaymentStatusUpdateCommand command) {
    return selectPaymentOrderStatus(command.getOrderId())
        .collectList()
        .flatMap(list -> insertPaymentHistory(list, command.getStatus(), command.getFailure().toString()))
        .flatMap(v -> updatePaymentOrderStatus(command.getOrderId(), command.getStatus()))
        .as(transactionalOperator::transactional)
        .thenReturn(true);
  }

  private Mono<Boolean> updatePaymentStatusToUnknown(PaymentStatusUpdateCommand command) {
    return selectPaymentOrderStatus(command.getOrderId())
        .collectList()
        .flatMap(list -> insertPaymentHistory(list, command.getStatus(), command.getFailure().toString()))
        .flatMap(v -> updatePaymentOrderStatus(command.getOrderId(), command.getStatus()))
        .flatMap(v -> incrementPaymentOrderFailedCount(command))
        .as(transactionalOperator::transactional)
        .thenReturn(true);
  }

  private Mono<List<Pair<Long, String>>> checkPreviousPaymentOrderStatus(String orderId) {
    return selectPaymentOrderStatus(orderId)
        .handle((Pair<Long, String> paymentOrder, SynchronousSink<Pair<Long, String>> sink) -> {
          switch (PaymentStatus.get(paymentOrder.getSecond())) {
            case NOT_STARTED, UNKNOWN, EXECUTING -> sink.next(paymentOrder);
            case SUCCESS -> sink.error(
                new PaymentAlreadyProcessedException(PaymentStatus.SUCCESS, "이미 처리 성공한 결제 입니다.")
            );
            case FAILURE -> sink.error(
                new PaymentAlreadyProcessedException(PaymentStatus.FAILURE, "이미 처리 실패한 결제 입니다.")
            );
          }
        })
        .collectList();
  }

  private Mono<Long> updatePaymentEventExtraDetails(PaymentStatusUpdateCommand command) {
    return databaseClient.sql(UPDATE_PAYMENT_EVENT_EXTRA_DETAILS_QUERY)
        .bind("orderName", command.getExtraDetails().getOrderName())
        .bind("method", command.getExtraDetails().getMethod().name())
        .bind("approvedAt", command.getExtraDetails().getApprovedAt().toString())
        .bind("orderId", command.getOrderId())
        .bind("type", command.getExtraDetails().getType())
        .bind("pspRawData", command.getExtraDetails().getPspRawData())
        .fetch()
        .rowsUpdated();
  }


  private Flux<Pair<Long, String>> selectPaymentOrderStatus(String orderId) {
    return databaseClient.sql(SELECT_PAYMENT_ORDER_STATUS_QUERY)
        .bind("orderId", orderId)
        .fetch()
        .all()
        .map(result -> Pair.of((Long) result.get("id"),(String) result.get("payment_order_status")));
  }

  private Mono<Long> insertPaymentHistory(
      List<Pair<Long, String>> paymentOrderIdToStatus,
      PaymentStatus status,
      String reason
  ) {
    if (paymentOrderIdToStatus.isEmpty()) {
      return Mono.empty();
    }

    // VALUES 절 만들기
    String valuesClauses = paymentOrderIdToStatus.stream()
        .map(pair -> String.format("( %d, '%s', '%s', '%s' )",
            pair.getFirst(),
            pair.getSecond(),
            status.name(),
            reason
        ))
        .collect(Collectors.joining(", "));

    // SQL 실행
    return databaseClient.sql(INSERT_PAYMENT_HISTORY_QUERY(valuesClauses))
        .fetch()
        .rowsUpdated();
  }

  private Mono<Long> updatePaymentOrderStatus(String orderId, PaymentStatus status) {
    return databaseClient.sql(UPDATE_PAYMENT_ORDER_STATUS_QUERY)
        .bind("orderId", orderId)
        .bind("status", status)
        .fetch()
        .rowsUpdated();
  }

  private Mono<Long> incrementPaymentOrderFailedCount(PaymentStatusUpdateCommand command) {
    return databaseClient.sql(INCREMENT_PAYMENT_ORDER_FAILED_COUNT_QUERY)
        .bind("orderId", command.getOrderId())
        .fetch()
        .rowsUpdated();
  }

  private Mono<?> updatePaymentKey(String orderId, String paymentKey) {
    return databaseClient.sql(UPDATE_PAYMENT_KEY_QUERY)
        .bind("orderId", orderId)
        .bind("paymentKey", paymentKey)
        .fetch()
        .rowsUpdated();
  }

  private static final String SELECT_PAYMENT_ORDER_STATUS_QUERY = """
      SELECT id, payment_order_status
      FROM payment_orders
      WHERE order_id = :orderId
      """;

  private static final String UPDATE_PAYMENT_ORDER_STATUS_QUERY = """
      UPDATE payment_orders
      SET payment_order_status = :status, updated_at = CURRENT_TIMESTAMP
      WHERE order_id = :orderId
      """;

  private static String INSERT_PAYMENT_HISTORY_QUERY(String valuesClauses) {
    return "INSERT INTO payment_order_history " +
        "(payment_order_id, payment_order_status, new_status, reason) " +
        "VALUES " + valuesClauses;
  }

  private static final String UPDATE_PAYMENT_KEY_QUERY = """
      UPDATE payment_events
      SET payment_key = :paymentKey
      WHERE order_id = :orderId
      """;

  private static final String UPDATE_PAYMENT_EVENT_EXTRA_DETAILS_QUERY = """
    UPDATE payment_events
    SET order_name = :orderName,
        method = :method,
        approved_at = :approvedAt,
        type = :type,
        updated_at = CURRENT_TIMESTAMP,
        psp_raw_data = :pspRawData
    WHERE order_id = :orderId
    """;

  private static final String INCREMENT_PAYMENT_ORDER_FAILED_COUNT_QUERY = """
    UPDATE payment_orders
    SET failed_count = failed_count + 1 
    WHERE order_id = :orderId
    """;
}
