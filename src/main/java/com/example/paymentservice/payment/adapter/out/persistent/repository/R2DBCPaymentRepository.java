package com.example.paymentservice.payment.adapter.out.persistent.repository;

import com.example.paymentservice.payment.domain.PaymentEvent;
import java.math.BigInteger;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Mono;

@Repository
@RequiredArgsConstructor
public class R2DBCPaymentRepository implements PaymentRepository{

  final String INSERT_PAYMENT_EVENT_QUERY = """
  INSERT INTO payment_events (buyer_id, order_name, order_id)
  VALUES (:buyerId, :orderName, :orderId)
""".trim();

  final String LAST_INSERT_ID_QUERY = """
      SELECT LAST_INSERT_ID()
      """.trim();

  // static 메서드로 SQL 문자열 생성
  public final String INSERT_PAYMENT_ORDER_QUERY(String valueClauses) {
    return """
            INSERT INTO payment_orders (payment_event_id, seller_id, order_id, product_id, amount, payment_order_status)
            VALUES %s
        """.formatted(valueClauses).stripIndent();
  }

  private final DatabaseClient databaseClient;
  private final TransactionalOperator transactionalOperator;

  @Override
  public Mono<Void> save(PaymentEvent paymentEvent) {
    return insertPaymentEvent(paymentEvent)
        .flatMap(rowsUpdated -> selectPaymentEventId())
        .flatMap(paymentEventId -> insertPaymentOrders(paymentEvent, paymentEventId))
        .as(transactionalOperator::transactional)
        .then(); // Mono<Void> 반환
  }

  private Mono<Long> insertPaymentOrders(PaymentEvent paymentEvent, Long paymentEventId) {
    String valueClauses = paymentEvent.getPaymentOrders().stream().map(paymentOrder -> {
      return String.format("(%s, %s, '%s', %s, %s, '%s')", paymentEventId,
          paymentOrder.getSellerId(), paymentOrder.getOrderId(), paymentOrder.getProductId(),
          paymentOrder.getAmount(), paymentOrder.getPaymentStatus());
    }).collect(Collectors.joining(",\n"));

    return databaseClient.sql(INSERT_PAYMENT_ORDER_QUERY(valueClauses))
        .fetch()
        .rowsUpdated();
  }

  private Mono<Long> selectPaymentEventId() {
    return databaseClient.sql(LAST_INSERT_ID_QUERY)
        .fetch()
        .first()
        .map(result -> ((BigInteger) result.get("LAST_INSERT_ID()")).longValue());
  }

  private Mono<Long> insertPaymentEvent(PaymentEvent paymentEvent) {
    return databaseClient.sql(INSERT_PAYMENT_EVENT_QUERY)
        .bind("buyerId", paymentEvent.getBuyerId())
        .bind("orderName", paymentEvent.getOrderName())
        .bind("orderId", paymentEvent.getOrderId())
        .fetch()
        .rowsUpdated();
  }


}
