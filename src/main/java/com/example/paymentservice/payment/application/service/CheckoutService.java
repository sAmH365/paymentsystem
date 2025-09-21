package com.example.paymentservice.payment.application.service;

import com.example.paymentservice.common.UseCase;
import com.example.paymentservice.payment.application.port.in.CheckoutCommand;
import com.example.paymentservice.payment.application.port.in.CheckoutUseCase;
import com.example.paymentservice.payment.application.port.out.LoadProductPort;
import com.example.paymentservice.payment.application.port.out.SavePaymentPort;
import com.example.paymentservice.payment.domain.CheckoutResult;
import com.example.paymentservice.payment.domain.PaymentEvent;
import com.example.paymentservice.payment.domain.PaymentOrder;
import com.example.paymentservice.payment.domain.PaymentStatus;
import com.example.paymentservice.payment.domain.Product;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Mono;

@UseCase
@RequiredArgsConstructor
public class CheckoutService implements CheckoutUseCase {

  private final LoadProductPort loadProductPort;
  private final SavePaymentPort savePaymentPort;

  @Override
  public Mono<CheckoutResult> checkout(CheckoutCommand command) {
    return loadProductPort.getProducts(command.getCartId(), command.getProductIds())
        .collectList()
        .map(products -> createPaymentEvent(command, products))
        .flatMap(paymentEvent -> savePaymentPort.save(paymentEvent).thenReturn(paymentEvent))
        .map(paymentEvent -> CheckoutResult.builder()
            .amount(paymentEvent.getTotalAmount())
            .orderId(paymentEvent.getOrderId())
            .orderName(paymentEvent.getOrderName())
            .build());
  }

  private PaymentEvent createPaymentEvent(CheckoutCommand command, List<Product> products) {
    return PaymentEvent.builder()
        .buyerId(command.getBuyerId())
        .orderId(command.getIdempotencyKey())
        .orderName(
            products.stream()
                .map(Product::getName)
                .collect(Collectors.joining(","))
        ) // products.joinToString {it.name}
        .paymentOrders(
            products.stream().map(product -> PaymentOrder.builder()
                    .sellerId(product.getSellerId())
                    .orderId(command.getIdempotencyKey())
                    .productId(product.getId())
                    .amount(product.getAmount().longValue())
                    .paymentStatus(PaymentStatus.NOT_STARTED)
                .build()).toList()
        )
        .build();
  }
}
