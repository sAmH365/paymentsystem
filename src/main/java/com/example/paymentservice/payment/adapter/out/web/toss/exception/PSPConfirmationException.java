package com.example.paymentservice.payment.adapter.out.web.toss.exception;

import com.example.paymentservice.payment.domain.PaymentStatus;
import lombok.Getter;

@Getter
public class PSPConfirmationException extends RuntimeException {

  private final String errorCode;
  private final String errorMessage;
  private final boolean isSuccess;
  private final boolean isFailure;
  private final boolean isUnknown;
  private final boolean isRetryableError;

  public PSPConfirmationException(
      String errorCode,
      String errorMessage,
      boolean isSuccess,
      boolean isFailure,
      boolean isUnknown,
      boolean isRetryableError,
      Throwable cause
  ) {
    super(errorMessage, cause);
    this.errorCode = errorCode;
    this.errorMessage = errorMessage;
    this.isSuccess = isSuccess;
    this.isFailure = isFailure;
    this.isUnknown = isUnknown;
    this.isRetryableError = isRetryableError;

    // Kotlin의 require(...) 대응
    if (!(isSuccess || isFailure || isUnknown)) {
      throw new IllegalArgumentException(
          this.getClass().getSimpleName() + " 는 올바르지 않은 결제 상태를 가지고 있습니다."
      );
    }
  }

  public PSPConfirmationException(
      String errorCode,
      String errorMessage,
      boolean isSuccess,
      boolean isFailure,
      boolean isUnknown,
      boolean isRetryableError
  ) {
    this(errorCode, errorMessage, isSuccess, isFailure, isUnknown, isRetryableError, null);
  }

  public PaymentStatus paymentStatus() {
    if (isSuccess) {
      return PaymentStatus.SUCCESS;
    } else if (isFailure) {
      return PaymentStatus.FAILURE;
    } else if (isUnknown) {
      return PaymentStatus.UNKNOWN;
    } else {
      throw new IllegalStateException(
          this.getClass().getSimpleName() + " 는 올바르지 않은 결제 상태를 가지고 있습니다."
      );
    }
  }
}
