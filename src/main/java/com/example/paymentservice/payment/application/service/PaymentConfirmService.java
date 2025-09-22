package com.example.paymentservice.payment.application.service;

import com.example.paymentservice.common.UseCase;
import com.example.paymentservice.payment.application.port.in.PaymentConfirmCommand;
import com.example.paymentservice.payment.application.port.in.PaymentConfirmUseCase;
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


  @Override
  public Mono<PaymentConfirmationResult> confirm(PaymentConfirmCommand command) {
    paymentStatusUpdatePort.updatePaymentStatusToExecuting(command.getOrderId(), command.getPaymentKey())
        .filterWhen(result -> paymentValidationPort.isValid(command.getOrderId(), command.getAmount()))
    ;
  }
}
