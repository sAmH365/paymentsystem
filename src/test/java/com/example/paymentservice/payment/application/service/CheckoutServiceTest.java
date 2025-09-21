package com.example.paymentservice.payment.application.service;

import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;
import static org.junit.jupiter.api.Assertions.*;

import com.example.paymentservice.payment.application.port.in.CheckoutCommand;
import com.example.paymentservice.payment.application.port.in.CheckoutUseCase;
import com.example.paymentservice.payment.domain.PaymentEvent;
import com.example.paymentservice.payment.domain.PaymentOrder;
import com.example.paymentservice.payment.test.PaymentDatabaseHelper;
import com.example.paymentservice.payment.test.PaymentTestConfiguration;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.test.StepVerifier;

@SpringBootTest
@Import(PaymentTestConfiguration.class)
class CheckoutServiceTest {

  @Autowired
  CheckoutUseCase checkoutUseCase;

  @Autowired
  PaymentDatabaseHelper paymentDatabaseHelper;

  @BeforeEach
  void setUp() {
    paymentDatabaseHelper.clean().block();
  }

  @Test
  void should_save_PaymentEvent_and_PaymentOrder_successfully() {
    String orderId = UUID.randomUUID().toString();
    CheckoutCommand checkoutCommand = CheckoutCommand.builder()
        .cartId(1L)
        .buyerId(1L)
        .productIds(List.of(1L, 2L, 3L))
        .idempotencyKey(orderId)
        .build();

    StepVerifier.create(checkoutUseCase.checkout(checkoutCommand))
        .expectNextMatches(checkoutResult -> {
          return checkoutResult.getAmount() == 6000L && checkoutResult.getOrderId().equals(orderId);
        }).verifyComplete();

    PaymentEvent paymentEvent = paymentDatabaseHelper.getPayments(orderId);

    assertThat(paymentEvent.getOrderId()).isEqualTo(orderId);
    assertThat(paymentEvent.getPaymentOrders().size()).isEqualTo(checkoutCommand.getProductIds().size());
    assertThat(paymentEvent.getTotalAmount()).isEqualTo(6000L);
    assertFalse(paymentEvent.isPaymentDone());
    // 모든 결제 주문이 ledgerUpdated가 false인지 확인
    assertTrue(paymentEvent.getPaymentOrders().stream()
        .noneMatch(PaymentOrder::isLedgerUpdated));

    // 모든 결제 주문이 walletUpdated가 false인지 확인
    assertTrue(paymentEvent.getPaymentOrders().stream()
        .noneMatch(PaymentOrder::isWalletUpdated));
  }
}
