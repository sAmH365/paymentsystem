package com.example.paymentservice.payment.domain;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentOrder {
  private Long id;
  private Long paymentEventId;
  private Long sellerId;
  private Long buyerId;
  private Long productId;
  private String orderId;
  private Long amount;
  private PaymentStatus paymentStatus;
  private boolean isLedgerUpdated;
  private boolean isWalletUpdated;
}
