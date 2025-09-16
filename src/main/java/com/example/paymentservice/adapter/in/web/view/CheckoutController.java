package com.example.paymentservice.adapter.in.web.view;

import com.example.paymentservice.common.WebAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import reactor.core.publisher.Mono;

@WebAdapter
@Controller
public class CheckoutController {

  @GetMapping("/")
  public Mono<String> checkoutPage() {
    return Mono.just("checkout");
  }
}
