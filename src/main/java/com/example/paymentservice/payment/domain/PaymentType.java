package com.example.paymentservice.payment.domain;

import lombok.Getter;


@Getter
public enum PaymentType {
  NORMAL("일반 결제");

  private final String description;

  PaymentType(String description) {
    this.description = description;
  }

  public static PaymentType get(String type) {
    return PaymentType.valueOf(type);
  }
}
