package com.example.paymentservice.payment.application.port.in;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentConfirmCommand {

  private String paymentKey;
  private String orderId;
  private Long amount;
}
