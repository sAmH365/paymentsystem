package com.example.paymentservice.payment.application.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.example.paymentservice.payment.application.port.in.CheckoutCommand;
import com.example.paymentservice.payment.application.port.in.CheckoutUseCase;
import com.example.paymentservice.payment.application.port.in.PaymentConfirmCommand;
import com.example.paymentservice.payment.application.port.out.PaymentExecutorPort;
import com.example.paymentservice.payment.application.port.out.PaymentStatusUpdatePort;
import com.example.paymentservice.payment.application.port.out.PaymentValidationPort;
import com.example.paymentservice.payment.domain.CheckoutResult;
import com.example.paymentservice.payment.domain.PSPConfirmationStatus;
import com.example.paymentservice.payment.domain.PaymentConfirmationResult;
import com.example.paymentservice.payment.domain.PaymentEvent;
import com.example.paymentservice.payment.domain.PaymentExecutionResult;
import com.example.paymentservice.payment.domain.PaymentExecutionResult.PaymentExtraDetails;
import com.example.paymentservice.payment.domain.PaymentMethod;
import com.example.paymentservice.payment.domain.PaymentStatus;
import com.example.paymentservice.payment.domain.PaymentType;
import com.example.paymentservice.payment.test.PaymentDatabaseHelper;
import com.example.paymentservice.payment.test.PaymentTestConfiguration;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.UUID;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import reactor.core.publisher.Mono;

@SpringBootTest
@Import(PaymentTestConfiguration.class)
public class PaymentConfirmServiceTest {

  @Autowired private CheckoutUseCase checkoutUseCase;
  @Autowired private PaymentStatusUpdatePort paymentStatusUpdatePort;
  @Autowired private PaymentValidationPort paymentValidationPort;
  @Autowired private PaymentDatabaseHelper paymentDatabaseHelper;

  @Mock
  private PaymentExecutorPort mockPaymentExecutorPort;

  @BeforeEach
  void setUp() {
    paymentDatabaseHelper.clean().block();
  }

  @Test
  void should_be_marked_as_SUCCESS_if_Payment_confirmation_success_in_PSP() {
    String orderId = UUID.randomUUID().toString();

    CheckoutCommand checkoutCommand = CheckoutCommand.builder()
        .cartId(1L)
        .buyerId(1L)
        .productIds(List.of(1L, 2L, 3L))
        .idempotencyKey(orderId)
        .build();

    CheckoutResult checkoutResult = checkoutUseCase.checkout(checkoutCommand).block();

    PaymentConfirmCommand paymentConfirmCommand = PaymentConfirmCommand.builder()
        .paymentKey(UUID.randomUUID().toString())
        .orderId(checkoutResult.getOrderId())
        .amount(checkoutResult.getAmount())
        .build();

    PaymentConfirmService paymentConfirmService = new PaymentConfirmService(
        paymentStatusUpdatePort, paymentValidationPort, mockPaymentExecutorPort
    );

    PaymentExecutionResult paymentExecutionResult = PaymentExecutionResult.builder()
        .paymentKey(paymentConfirmCommand.getPaymentKey())
        .orderId(paymentConfirmCommand.getOrderId())
        .extraDetails(PaymentExtraDetails.builder()
            .type(PaymentType.NORMAL)
            .method(PaymentMethod.EASY_PAY)
            .totalAmount(paymentConfirmCommand.getAmount())
            .orderName("test order name")
            .pspConfirmationStatus(PSPConfirmationStatus.DONE)
            .approvedAt(LocalDateTime.now())
            .pspRawData("{}")
            .build())
        .isSuccess(true)
        .isFailure(false)
        .isUnknown(false)
        .isUnknown(false)
        .build();

    Mockito.when(mockPaymentExecutorPort.execute(paymentConfirmCommand))
        .thenReturn(Mono.just(paymentExecutionResult));

    // Act
    PaymentConfirmationResult paymentConfirmationResult =
        paymentConfirmService.confirm(paymentConfirmCommand).block();

    PaymentEvent paymentEvent = paymentDatabaseHelper.getPayments(orderId);

    // Assert
    Assertions.assertNotNull(paymentConfirmationResult);
    assertThat(paymentConfirmationResult.getStatus()).isEqualTo(PaymentStatus.SUCCESS);
//    assertTrue(paymentEvent.isSuccess());
    assertThat(paymentEvent.getPaymentType()).isEqualTo(paymentExecutionResult.getExtraDetails().getType());
    assertThat(paymentEvent.getPaymentMethod()).isEqualTo(paymentExecutionResult.getExtraDetails().getMethod());
    assertThat(paymentEvent.getOrderName()).isEqualTo(paymentExecutionResult.getExtraDetails().getOrderName());
    assertThat(paymentEvent.getApprovedAt().truncatedTo(ChronoUnit.MINUTES))
        .isEqualTo(paymentExecutionResult.getExtraDetails().getApprovedAt().truncatedTo(ChronoUnit.MINUTES));
  }

}
