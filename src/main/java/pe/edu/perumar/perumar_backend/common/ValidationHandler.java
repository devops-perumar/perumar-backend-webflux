// src/main/java/pe/edu/perumar/perumar_backend/common/ValidationHandler.java
package pe.edu.perumar.perumar_backend.common;

import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.support.WebExchangeBindException;
import reactor.core.publisher.Mono;

import java.util.Map;
import java.util.stream.Collectors;

@RestControllerAdvice
public class ValidationHandler {

  @ExceptionHandler(WebExchangeBindException.class)
  @ResponseStatus(HttpStatus.UNPROCESSABLE_ENTITY) // 422
  public Mono<Map<String, Object>> handleBind(WebExchangeBindException ex) {
    var errors = ex.getFieldErrors().stream().map(fe -> Map.of(
        "field", fe.getField(),
        "rejected", fe.getRejectedValue(),
        "message", fe.getDefaultMessage()
    )).collect(Collectors.toList());
    return Mono.just(Map.of(
        "status", 422,
        "error", "Validation Failed",
        "errors", errors
    ));
  }
}
