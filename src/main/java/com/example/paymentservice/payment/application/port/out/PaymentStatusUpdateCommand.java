package com.example.paymentservice.payment.application.port.out;

import com.example.paymentservice.payment.domain.PaymentExecutionResult;
import com.example.paymentservice.payment.domain.PaymentExecutionResult.PaymentExtraDetails;
import com.example.paymentservice.payment.domain.PaymentExecutionResult.PaymentFailure;
import com.example.paymentservice.payment.domain.PaymentStatus;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentStatusUpdateCommand {

  private final String paymentKey;
  private final String orderId;
  private final PaymentStatus status;
  private final PaymentExtraDetails extraDetails;
  private final PaymentFailure failure;

  // 주 생성자 (검증 포함)
  @Builder
  public PaymentStatusUpdateCommand(
      String paymentKey,
      String orderId,
      PaymentStatus status,
      PaymentExtraDetails extraDetails,
      PaymentFailure failure
  ) {
    this.paymentKey = paymentKey;
    this.orderId = orderId;
    this.status = status;
    this.extraDetails = extraDetails;
    this.failure = failure;

    // 검증 로직 (코틀린 init 블록 대응)
    if (!(status == PaymentStatus.SUCCESS ||
        status == PaymentStatus.FAILURE ||
        status == PaymentStatus.UNKNOWN)) {
      throw new IllegalArgumentException(
          String.format("결제 상태 (status: %s) 는 올바르지 않은 결제 상태입니다.", status)
      );
    }

    if (status == PaymentStatus.SUCCESS && extraDetails == null) {
      throw new IllegalArgumentException(
          "PaymentStatus 값이 SUCCESS 라면 PaymentExtraDetails 는 null 이 되면 안됩니다."
      );
    }

    if (status == PaymentStatus.FAILURE && failure == null) {
      throw new IllegalArgumentException(
          "PaymentStatus 값이 FAILURE 라면 PaymentFailure 는 null 이 되면 안됩니다."
      );
    }
  }

  // 보조 생성자 (PaymentExecutionResult → PaymentStatusUpdateCommand 변환)
  public PaymentStatusUpdateCommand(PaymentExecutionResult result) {
    this(
        result.getPaymentKey(),
        result.getOrderId(),
        result.getPaymentStatus(),
        result.getExtraDetails(),
        result.getFailure()
    );
  }
}
