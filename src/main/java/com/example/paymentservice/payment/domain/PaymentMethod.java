package com.example.paymentservice.payment.domain;

import lombok.Getter;

@Getter
public enum PaymentMethod {

  EASY_PAY("간편 결제");

  private final String description;

  PaymentMethod(String description) {
    this.description = description;
  }

  public static PaymentMethod get(String type) {
    return PaymentMethod.valueOf(type);
  }
}
