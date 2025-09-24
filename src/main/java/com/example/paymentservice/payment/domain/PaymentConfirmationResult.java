package com.example.paymentservice.payment.domain;

import com.example.paymentservice.payment.domain.PaymentExecutionResult.PaymentFailure;
import lombok.Getter;

@Getter
public class PaymentConfirmationResult {

  private final PaymentStatus status;
  private final PaymentFailure failure;
  private final String message;

  public PaymentConfirmationResult(PaymentStatus status, PaymentFailure failure) {
    this.status = status;
    this.failure = failure;

    if (status == PaymentStatus.FAILURE && failure == null) {
      throw new IllegalArgumentException(
          "결제 상태 FAILURE 일 때 PaymentExecutionFailure 는 null 값이 될 수 없습니다."
      );
    }

    // message 초기화 (코틀린 when 블록 대응)
    switch (status) {
      case SUCCESS -> this.message = "결제 처리에 성공하였습니다.";
      case FAILURE -> this.message = "결제 처리에 실패하였습니다.";
      case UNKNOWN -> this.message = "결제 처리 중에 알 수 없는 에러가 발생하였습니다.";
      default -> throw new IllegalStateException(
          String.format("현재 결제 상태 (status: %s) 는 올바르지 않은 상태입니다.", status)
      );
    }
  }

}
