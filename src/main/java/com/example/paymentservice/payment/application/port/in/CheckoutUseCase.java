package com.example.paymentservice.payment.application.port.in;

import com.example.paymentservice.payment.domain.CheckoutResult;
import reactor.core.publisher.Mono;

public interface CheckoutUseCase {

  Mono<CheckoutResult> checkout(CheckoutCommand command);
}
