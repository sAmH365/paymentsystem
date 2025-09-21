package com.example.paymentservice.payment.adapter.out.persistent;

import com.example.paymentservice.common.PersistentAdapter;
import com.example.paymentservice.payment.adapter.out.persistent.repository.PaymentRepository;
import com.example.paymentservice.payment.application.port.out.SavePaymentPort;
import com.example.paymentservice.payment.domain.PaymentEvent;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@PersistentAdapter
@RequiredArgsConstructor
public class PaymentPersistentAdapter implements SavePaymentPort {

  private final PaymentRepository paymentRepository;

  @Override
  public Mono<Void> save(PaymentEvent paymentEvent) {
    return paymentRepository.save(paymentEvent);
  }
}
