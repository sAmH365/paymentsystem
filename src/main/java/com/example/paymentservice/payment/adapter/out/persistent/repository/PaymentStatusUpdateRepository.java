package com.example.paymentservice.payment.adapter.out.persistent.repository;

import reactor.core.publisher.Mono;

public interface PaymentStatusUpdateRepository {

  Mono<Boolean> updatePaymentStatusToExecuting(String orderId, String paymentKey);
}
