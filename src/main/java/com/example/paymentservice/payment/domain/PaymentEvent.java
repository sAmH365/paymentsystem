package com.example.paymentservice.payment.domain;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentEvent {

  private Long id;
  private Long buyerId;
  private String orderName;
  private String orderId;
  private String paymentKey;
  private PaymentType paymentType;
  private PaymentMethod paymentMethod;
  private LocalDateTime approvedAt;
  private boolean isPaymentDone;

  @Builder.Default
  private List<PaymentOrder> paymentOrders = new ArrayList<>();

  public Long getTotalAmount() {
    return paymentOrders.stream()
        .mapToLong(PaymentOrder::getAmount)
        .sum();
  }
}
