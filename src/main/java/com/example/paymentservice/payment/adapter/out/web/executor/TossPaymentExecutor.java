package com.example.paymentservice.payment.adapter.out.web.executor;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@Component
@RequiredArgsConstructor
public class TossPaymentExecutor {

  private final WebClient tossPayemntWebClient;
  private final String uri = "/v1/payments/confirm";


  public Mono<String> execute(String paymentKey, String orderId, String amount) {
    String param = String.format("""
        {
          "paymentKey": "%s",
          "orderId": "%s",
          "amount": "%s"
        }
        """, paymentKey, orderId, amount).trim();

    return tossPayemntWebClient.post()
        .uri(uri)
        .bodyValue(param)
        .retrieve()
        .bodyToMono(String.class);
  }
}
