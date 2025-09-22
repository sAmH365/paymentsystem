package com.example.paymentservice.payment.adapter.in.web.api;

import com.example.paymentservice.payment.adapter.in.web.request.TossPaymentConfirmRequest;
import com.example.paymentservice.payment.adapter.in.web.response.ApiResponse;
import com.example.paymentservice.common.WebAdapter;
import com.example.paymentservice.payment.application.port.in.PaymentConfirmCommand;
import com.example.paymentservice.payment.application.port.in.PaymentConfirmUseCase;
import com.example.paymentservice.payment.domain.PaymentConfirmationResult;
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

  private final PaymentConfirmUseCase paymentConfirmUseCase;

  @PostMapping("/confirm")
  public Mono<ResponseEntity<ApiResponse<PaymentConfirmationResult>>> confirm(@RequestBody TossPaymentConfirmRequest request) {
    PaymentConfirmCommand command = PaymentConfirmCommand.builder()
        .paymentKey(request.getPaymentKey())
        .orderId(request.getOrderId())
        .amount(request.getAmount())
        .build();

    return paymentConfirmUseCase.confirm(command)
        .map(result -> ResponseEntity.ok().body(ApiResponse.with(HttpStatus.OK, "ok", result)));
  }

}
