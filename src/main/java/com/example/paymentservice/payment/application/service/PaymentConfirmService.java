package com.example.paymentservice.payment.application.service;

import com.example.paymentservice.common.UseCase;
import com.example.paymentservice.payment.application.port.in.PaymentConfirmCommand;
import com.example.paymentservice.payment.application.port.in.PaymentConfirmUseCase;
import com.example.paymentservice.payment.application.port.out.PaymentExecutorPort;
import com.example.paymentservice.payment.application.port.out.PaymentStatusUpdateCommand;
import com.example.paymentservice.payment.application.port.out.PaymentStatusUpdatePort;
import com.example.paymentservice.payment.application.port.out.PaymentValidationPort;
import com.example.paymentservice.payment.domain.PaymentConfirmationResult;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCase
@RequiredArgsConstructor
public class PaymentConfirmService implements PaymentConfirmUseCase {

  private final PaymentStatusUpdatePort paymentStatusUpdatePort;
  private final PaymentValidationPort paymentValidationPort;
  private final PaymentExecutorPort paymentExecutorPort;

//  private final PaymentErrorHandler paymentErrorHandler;


  @Override
  public Mono<PaymentConfirmationResult> confirm(PaymentConfirmCommand command) {
    return paymentStatusUpdatePort.updatePaymentStatusToExecuting(command.getOrderId(), command.getPaymentKey())
        .filterWhen(result -> paymentValidationPort.isValid(command.getOrderId(), command.getAmount()))
        .flatMap(result -> paymentExecutorPort.execute(command))
        .flatMap(result ->
            paymentStatusUpdatePort.updatePaymentStatus(
                PaymentStatusUpdateCommand.builder()
                    .paymentKey(result.getPaymentKey())
                    .orderId(result.getOrderId())
                    .status(result.getPaymentStatus())
                    .extraDetails(result.getExtraDetails())
                    .failure(result.getFailure())
                    .build()
            ).thenReturn(result)
        )
        .map(result -> new PaymentConfirmationResult(result.getPaymentStatus(), result.getFailure()));
//        .onErrorResume(error -> paymentErrorHandler.handlePaymentConfirmationError(error, command));
  }
}
