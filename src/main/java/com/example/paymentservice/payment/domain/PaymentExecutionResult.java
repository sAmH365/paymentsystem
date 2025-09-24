package com.example.paymentservice.payment.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
public class PaymentExecutionResult {

  private String paymentKey;
  private String orderId;
  private PaymentExtraDetails extraDetails;
  private PaymentFailure failure;
  private Boolean isSuccess;
  private Boolean isFailure;
  private Boolean isUnknown;
  private Boolean isRetryable;

  @Builder
  public PaymentExecutionResult(String paymentKey,
      String orderId,
      PaymentExtraDetails extraDetails,
      PaymentFailure failure,
      Boolean isSuccess,
      Boolean isFailure,
      Boolean isUnknown,
      Boolean isRetryable) {
    this.paymentKey = paymentKey;
    this.orderId = orderId;
    this.extraDetails = extraDetails;
    this.failure = failure;
    this.isSuccess = isSuccess;
    this.isFailure = isFailure;
    this.isUnknown = isUnknown;
    this.isRetryable = isRetryable;

    if (!(Boolean.TRUE.equals(isSuccess) || Boolean.TRUE.equals(isFailure) || Boolean.TRUE.equals(
        isUnknown))) {
      throw new IllegalArgumentException(
          String.format("결제 (orderId: %s) 는 올바르지 않은 결제 상태입니다.", orderId)
      );
    }
  }

  public PaymentStatus getPaymentStatus() {
    if (this.isSuccess) {
      return PaymentStatus.SUCCESS;
    } else if (this.isFailure) {
      return PaymentStatus.FAILURE;
    } else if(this.isUnknown) {
      return PaymentStatus.UNKNOWN;
    } else {
      throw new IllegalArgumentException(
          String.format("결제 (orderId: %s) 는 올바르지 않은 결제 상태입니다.", orderId)
      );
    }
  }

  @Builder
  @Getter
  public static class PaymentExtraDetails {
    private PaymentType type;
    private PaymentMethod method;
    private LocalDateTime approvedAt;
    private String orderName;
    private PSPConfirmationStatus pspConfirmationStatus;
    private Long totalAmount;
    private String pspRawData;
  }

  @Builder
  @Getter
  public static class PaymentFailure {
    private String errorCode;
    private String message;
  }
}
