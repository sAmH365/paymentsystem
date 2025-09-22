package com.example.paymentservice.payment.adapter.out.web.toss.executor;

import com.example.paymentservice.payment.adapter.out.web.toss.executor.response.TossPaymentConfirmationResponse;
import com.example.paymentservice.payment.application.port.in.PaymentConfirmCommand;
import com.example.paymentservice.payment.domain.PaymentExecutionResult;
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

    tossPayemntWebClient.post()
        .uri(uri)
        .header("Idempotency-Key", command.getOrderId())
        .bodyValue(param)
        .retrieve()
        .bodyToMono(TossPaymentConfirmationResponse.class)
        .map(response -> PaymentExecutionResult.builder())

    ;
  }
}
