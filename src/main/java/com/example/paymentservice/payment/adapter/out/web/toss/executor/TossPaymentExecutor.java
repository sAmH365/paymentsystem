package com.example.paymentservice.payment.adapter.out.web.toss.executor;

import com.example.paymentservice.payment.adapter.out.web.toss.executor.response.TossPaymentConfirmationResponse;
import com.example.paymentservice.payment.application.port.in.PaymentConfirmCommand;
import com.example.paymentservice.payment.domain.PSPConfirmationStatus;
import com.example.paymentservice.payment.domain.PaymentExecutionResult;
import com.example.paymentservice.payment.domain.PaymentExecutionResult.PaymentExtraDetails;
import com.example.paymentservice.payment.domain.PaymentMethod;
import com.example.paymentservice.payment.domain.PaymentType;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TossPaymentExecutor implements PaymentExecutor{

  private final WebClient tossPayemntWebClient;
  private final String uri = "/v1/payments/confirm";

  @Override
  public Mono<PaymentExecutionResult> execute(PaymentConfirmCommand command) {
    String param = String.format("""
        {
          "paymentKey": "%s",
          "orderId": "%s",
          "amount": "%s"
        }
        """, command.getPaymentKey(), command.getOrderId(), command.getAmount()).stripIndent();

    return tossPayemntWebClient.post()
        .uri(uri)
        .header("Idempotency-Key", command.getOrderId())
        .bodyValue(param)
        .retrieve()
        .bodyToMono(TossPaymentConfirmationResponse.class)
        .map(response -> PaymentExecutionResult.builder()
            .paymentKey(command.getPaymentKey())
            .orderId(command.getOrderId())
            .extraDetails(PaymentExtraDetails.builder()
                .type(PaymentType.get(response.getType()))
                .method(PaymentMethod.get(response.getMethod()))
                .approvedAt(LocalDateTime.parse(response.getApprovedAt(), DateTimeFormatter.ISO_OFFSET_DATE_TIME))
                .pspRawData(response.toString())
                .orderName(response.getOrderName())
                .pspConfirmationStatus(PSPConfirmationStatus.get(response.getStatus()))
                .totalAmount(Long.valueOf(response.getTotalAmount()))
                .build())
            .isSuccess(true)
            .isFailure(false)
            .isUnknown(false)
            .isRetryable(false)
            .build());
  }
}
