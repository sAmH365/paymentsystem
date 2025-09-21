package com.example.paymentservice.payment.adapter.out.web.product.client;

import com.example.paymentservice.payment.domain.Product;
import java.util.List;
import reactor.core.publisher.Flux;

public interface ProductClient {

  Flux<Product> getProducts(Long cartId, List<Long> productIds);
}
