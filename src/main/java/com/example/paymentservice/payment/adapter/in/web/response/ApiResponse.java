package com.example.paymentservice.payment.adapter.in.web.response;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import org.springframework.http.HttpStatus;

@Getter
@Builder
@AllArgsConstructor
public class ApiResponse<T> {
  private Integer status = 200;
  private String message = "";
  private T data = null;

  public static <T> ApiResponse<T> with(HttpStatus httpStatus, String message, T data) {
    return new ApiResponse<>(httpStatus.value(), message, data);
  }
}
