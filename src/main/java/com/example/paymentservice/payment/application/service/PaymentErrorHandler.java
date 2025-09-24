package com.example.paymentservice.payment.application.service;

import com.example.paymentservice.payment.adapter.out.persistent.exception.PaymentAlreadyProcessedException;
import com.example.paymentservice.payment.adapter.out.persistent.exception.PaymentValidationException;
import com.example.paymentservice.payment.adapter.out.web.toss.exception.PSPConfirmationException;
import com.example.paymentservice.payment.application.port.in.PaymentConfirmCommand;
import com.example.paymentservice.payment.application.port.out.PaymentStatusUpdateCommand;
import com.example.paymentservice.payment.application.port.out.PaymentStatusUpdatePort;
import com.example.paymentservice.payment.domain.PaymentConfirmationResult;
import com.example.paymentservice.payment.domain.PaymentExecutionResult.PaymentFailure;
import com.example.paymentservice.payment.domain.PaymentStatus;
import java.util.concurrent.TimeoutException;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;

@Service
public class PaymentErrorHandler
//    implements PaymentStatusUpdatePort
{

//  private final PaymentStatusUpdatePort paymentStatusUpdatePort;
//
//  public PaymentErrorHandler(PaymentStatusUpdatePort paymentStatusUpdatePort) {
//    this.paymentStatusUpdatePort = paymentStatusUpdatePort;
//  }
//
//  public Mono<PaymentConfirmationResult> handlePaymentConfirmationError(
//      Throwable error,
//      PaymentConfirmCommand command
//  ) {
//    PaymentStatus status;
//    PaymentFailure failure;
//
//    if (error instanceof PSPConfirmationException e) {
//      status = e.paymentStatus();
//      failure = PaymentFailure.builder()
//          .errorCode(e.getErrorCode())
//          .message(e.getErrorMessage())
//          .build();
//    } else if (error instanceof PaymentValidationException e) {
//      status = PaymentStatus.FAILURE;
//      failure =PaymentFailure.builder()
//          .errorCode(e.getErrorCode())
//          .message(e.getErrorMessage())
//          .build();
//
//          new PaymentFailure(
//          e.getClass().getSimpleName(),
//          e.getMessage() != null ? e.getMessage() : ""
//      );
//    } else if (error instanceof PaymentAlreadyProcessedException e) {
//      return Mono.just(
//          new PaymentConfirmationResult(
//              e.getStatus(),
//              new PaymentFailure(
//                  e.getClass().getSimpleName(),
//                  e.getMessage() != null ? e.getMessage() : ""
//              )
//          )
//      );
//    } else if (error instanceof TimeoutException e) {
//      status = PaymentStatus.UNKNOWN;
//      failure = new PaymentFailure(
//          e.getClass().getSimpleName(),
//          e.getMessage() != null ? e.getMessage() : ""
//      );
//    } else {
//      status = PaymentStatus.UNKNOWN;
//      failure = new PaymentFailure(
//          error.getClass().getSimpleName(),
//          error.getMessage() != null ? error.getMessage() : ""
//      );
//    }
//
//    PaymentStatusUpdateCommand paymentStatusUpdateCommand = PaymentStatusUpdateCommand.builder()
//        .paymentKey(command.getPaymentKey())
//        .orderId(command.getOrderId())
//        .status(status)
//        .failure(failure)
//        .build();
//
//    return paymentStatusUpdatePort.updatePaymentStatus(paymentStatusUpdateCommand)
//        .map(result -> new PaymentConfirmationResult(status, failure));
//  }
}
