package com.example.paymentservice.payment.adapter.in.web.view;

import com.example.paymentservice.common.IdempotencyCreator;
import com.example.paymentservice.common.WebAdapter;
import com.example.paymentservice.payment.adapter.in.web.request.CheckoutRequest;
import com.example.paymentservice.payment.application.port.in.CheckoutCommand;
import com.example.paymentservice.payment.application.port.in.CheckoutUseCase;
import com.example.paymentservice.payment.domain.CheckoutResult;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@WebAdapter
@Controller
@RequiredArgsConstructor
public class CheckoutController {

  private final CheckoutUseCase checkoutUseCase;

  @GetMapping("/")
  public Mono<String> checkoutPage(CheckoutRequest request, Model model) {
    CheckoutCommand command = CheckoutCommand.builder()
        .cartId(request.getCartId())
        .buyerId(request.getBuyerId())
        .productIds(request.getProductIds())
        .idempotencyKey(IdempotencyCreator.create(request.getSeed()))
        .build();

    Mono<CheckoutResult> checkout = checkoutUseCase.checkout(command);
    return checkout.map(result -> {
      model.addAttribute("orderId", result.getOrderId());
      model.addAttribute("orderName", result.getOrderName());
      model.addAttribute("amount", result.getAmount());

      return "checkout";
    });
  }
}
