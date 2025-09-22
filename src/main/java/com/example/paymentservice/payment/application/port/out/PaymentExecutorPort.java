package com.example.paymentservice.payment.application.port.out;

import com.example.paymentservice.payment.application.port.in.PaymentConfirmCommand;
import reactor.core.publisher.Mono;

public interface PaymentExecutorPort {

  Mono<PaymentExecutionResult> execute(PaymentConfirmCommand command);

}
