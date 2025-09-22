package com.example.paymentservice.payment.application.port.in;

import com.example.paymentservice.payment.domain.PaymentConfirmationResult;
import reactor.core.publisher.Mono;

public interface PaymentConfirmUseCase {

  Mono<PaymentConfirmationResult> confirm(PaymentConfirmCommand command);
}
