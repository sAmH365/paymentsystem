package com.example.paymentservice.payment.adapter.out.web.product;

import com.example.paymentservice.common.WebAdapter;
import com.example.paymentservice.payment.adapter.out.web.product.client.ProductClient;
import com.example.paymentservice.payment.application.port.out.LoadProductPort;
import com.example.paymentservice.payment.domain.Product;
import java.util.List;
import lombok.RequiredArgsConstructor;
import reactor.core.publisher.Flux;

@WebAdapter
@RequiredArgsConstructor
public class ProductWebAdapter implements LoadProductPort {

  private final ProductClient productClient;

  @Override
  public Flux<Product> getProducts(Long cartId, List<Long> productIds) {
    return productClient.getProducts(cartId, productIds);
  }
}
