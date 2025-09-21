package com.example.paymentservice.payment.domain;

import java.math.BigDecimal;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
public class Product {

  private Long id;
  private BigDecimal amount;
  private Integer quantity;
  private String name;
  private Long sellerId;
}
