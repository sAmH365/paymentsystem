package com.example.paymentservice.payment.domain;

import java.time.LocalDateTime;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class PaymentExecutionResult {

  private String paymentKey;
  private String orderId;
  private PaymentExtraDetails extraDetails;
  private Failure failure;
  private Boolean isSuccess;
  private Boolean isFailure;
  private Boolean isUnknown;
  private Boolean isRetryable;

  public PaymentExecutionResult() {

  }

  @Builder
  @Getter
  public class PaymentExtraDetails {
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
  public class Failure {
    private String errorCode;
    private String message;
  }
}
