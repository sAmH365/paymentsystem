package com.example.paymentservice.payment.application.port.in;

import java.util.List;
import lombok.Builder;
import lombok.Getter;

/**
 * 멱등성을 가지기 위한 클래스
 * 같은 요청은 한번만 처리된다
 */
@Builder
@Getter
public class CheckoutCommand {
  private Long cartId;
  private Long buyerId;
  private List<Long> productIds;
  private String idempotencyKey;
}
