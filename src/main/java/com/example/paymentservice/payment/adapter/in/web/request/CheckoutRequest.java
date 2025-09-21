package com.example.paymentservice.payment.adapter.in.web.request;

import java.time.LocalDateTime;
import java.util.List;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class CheckoutRequest {
  private Long cartId;
  private List<Long> productIds;
  private Long buyerId;

  // 현재 주문내용과 미래의 주문 내용을 구분하기위한 변수
  // 현재 주문내용과 미래의 주문내용이 같을경우 요청을 구분할 수 있는 값 필요
  private String seed;
}
