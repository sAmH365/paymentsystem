package com.example.paymentservice.payment.adapter.out.web.product.client;

import com.example.paymentservice.payment.domain.Product;
import java.math.BigDecimal;
import java.util.List;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;

@Component
public class MockProductClient implements ProductClient {

  @Override
  public Flux<Product> getProducts(Long cartId, List<Long> productIds) {
    return Flux.fromIterable(
        productIds.stream().map(productId -> {
          return Product.builder()
              .id(productId)
              .amount(new BigDecimal(productId * 1000))
              .quantity(2)
              .name("test_product_" + productId)
              .sellerId(1L)
              .build();
        }).toList()
    );
  }
}
