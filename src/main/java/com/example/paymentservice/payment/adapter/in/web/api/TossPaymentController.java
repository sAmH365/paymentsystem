package com.example.paymentservice.payment.adapter.in.web.api;

import com.example.paymentservice.payment.adapter.in.web.request.TossPaymentConfirmRequest;
import com.example.paymentservice.payment.adapter.in.web.response.ApiResponse;
import com.example.paymentservice.payment.adapter.out.web.executor.TossPaymentExecutor;
import com.example.paymentservice.common.WebAdapter;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@Slf4j
@WebAdapter
@RequestMapping("/v1/toss")
@RestController
@RequiredArgsConstructor
public class TossPaymentController {

  private final TossPaymentExecutor tossPaymentExecutor;

  @PostMapping("/confirm")
  public Mono<ResponseEntity<ApiResponse<String>>> confirm(@RequestBody TossPaymentConfirmRequest request) {

    log.info("TossPaymentConfirmRequest : {}", request);

    return tossPaymentExecutor.execute(request.getPaymentKey(), request.getOrderId(), request.getAmount().toString())
        .map(str -> ResponseEntity.ok().body(ApiResponse.with(HttpStatus.OK, "ok", str)));
  }

}
