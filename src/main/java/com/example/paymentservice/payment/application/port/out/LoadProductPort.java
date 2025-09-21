package com.example.paymentservice.payment.application.port.out;

import com.example.paymentservice.payment.domain.Product;
import java.util.List;
import reactor.core.publisher.Flux;

public interface LoadProductPort {

  Flux<Product> getProducts(Long cartId, List<Long> productIds);
}
