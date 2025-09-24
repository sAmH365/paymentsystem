package com.example.paymentservice.payment.test;

import com.example.paymentservice.payment.domain.PaymentEvent;
import com.example.paymentservice.payment.domain.PaymentMethod;
import com.example.paymentservice.payment.domain.PaymentOrder;
import com.example.paymentservice.payment.domain.PaymentStatus;
import com.example.paymentservice.payment.domain.PaymentType;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.ZonedDateTime;
import java.util.Map;
import java.util.Optional;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

public class R2DBCPaymentDatabaseHelper implements PaymentDatabaseHelper{

  private final DatabaseClient databaseClient;
  private final TransactionalOperator transactionalOperator;

  public R2DBCPaymentDatabaseHelper(DatabaseClient databaseClient,
      TransactionalOperator transactionalOperator) {
    this.databaseClient = databaseClient;
    this.transactionalOperator = transactionalOperator;
  }

  @Override
  public PaymentEvent getPayments(String orderId) {
    return databaseClient.sql(SELECT_PAYMENT_QUERY)
        .bind("orderId", orderId)
        .fetch()
        .all()
        .groupBy(row -> (Long) row.get("payment_event_id"))
        .flatMap(groupedFlux ->
            groupedFlux.collectList()
                .map(results -> {
                  Map<String, Object> first = results.get(0);
                  return PaymentEvent.builder()
                      .id(groupedFlux.key())
                      .orderId((String) first.get("order_id"))
                      .orderName((String) first.get("order_name"))
                      .buyerId((Long) first.get("buyer_id"))
                      .paymentKey((String) first.get("payment_key"))
                      .paymentType(first.get("type") != null ? PaymentType.get((String) first.get("type")) : null)
                      .paymentMethod(first.get("method") != null ? PaymentMethod.valueOf((String) first.get("method")) : null)
                      .approvedAt(first.get("approved_at") != null
                          ? ((LocalDateTime) first.get("approved_at"))
                          : null)
                      .isPaymentDone((Boolean) (first.get("is_payment_done")))
                      .paymentOrders(results.stream()
                          .map(result -> PaymentOrder.builder()
                              .id((Long) result.get("id"))
                              .paymentEventId(groupedFlux.key())
                              .sellerId((Long) result.get("seller_id"))
                              .productId((Long) result.get("product_id"))
                              .orderId((String) result.get("order_id"))
                              .amount(((BigDecimal) result.get("amount")).longValue())
                              .paymentStatus(PaymentStatus.get((String) result.get("payment_order_status")))
                              .isLedgerUpdated(Optional.ofNullable((Boolean) result.get("ledger_updated")).orElse(false))
                              .isWalletUpdated(Optional.ofNullable((Boolean) result.get("wallet_updated")).orElse(false))
                              .build()
                          )
                          .toList())
                      .build();
                })
        )
        .next()   // 첫 번째 PaymentEvent 가져오기
        .block();
  }

  @Override
  public Mono<Void> clean() {
    return deletePaymentOrderHistories()
        .flatMap(result -> deletePaymentOrders())
        .flatMap(result -> deletePaymentEvent())
        .as(transactionalOperator::transactional)
        .then();
  }

  private Mono<Long> deletePaymentEvent() {
    return databaseClient.sql(DELETE_PAYMENT_EVENT_QUERY)
        .fetch()
        .rowsUpdated();
  }


  private Mono<Long> deletePaymentOrderHistories() {
    return databaseClient.sql(DELETE_PAYMENT_ORDER_HISTORY_QUERY)
        .fetch()
        .rowsUpdated();
  }

  private Mono<Long> deletePaymentOrders() {
    return databaseClient.sql(DELETE_PAYMENT_ORDER_QUERY)
        .fetch()
        .rowsUpdated();
  }

  final String SELECT_PAYMENT_QUERY = """
      SELECT * FROM payment_events pe
      INNER JOIN payment_orders po ON pe.order_id = po.order_id
      WHERE pe.order_id = :orderId
      """;

  final String DELETE_PAYMENT_EVENT_QUERY = """
      DELETE FROM payment_events
      """;
  final String DELETE_PAYMENT_ORDER_QUERY = """
      DELETE FROM payment_orders
      """;

  final String DELETE_PAYMENT_ORDER_HISTORY_QUERY = """
      DELETE FROM payment_order_history
      """;
}
