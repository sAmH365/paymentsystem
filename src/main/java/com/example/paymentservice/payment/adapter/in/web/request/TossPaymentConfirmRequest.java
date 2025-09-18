package com.example.paymentservice.payment.adapter.in.web.request;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

@Getter
@ToString
public class TossPaymentConfirmRequest {

  private String paymentKey;
  private String orderId;
  private Long amount;
}
