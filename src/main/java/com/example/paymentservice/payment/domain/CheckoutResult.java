package com.example.paymentservice.payment.domain;

import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckoutResult {

  private Long amount;
  private String orderId;
  private String orderName;
}
