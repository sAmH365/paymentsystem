package com.example.paymentservice.payment.adapter.in.web.view;

import com.example.paymentservice.common.WebAdapter;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import reactor.core.publisher.Mono;

@Controller
@WebAdapter
@RequestMapping
public class PaymentController {

  @GetMapping("/success")
  public Mono<String> successPage() {
    return Mono.just("success");
  }

  @GetMapping("/fail")
  public Mono<String> failPage() {
    return Mono.just("fail");
  }
}
