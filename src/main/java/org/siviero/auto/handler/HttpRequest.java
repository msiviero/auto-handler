package org.siviero.auto.handler;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.undertow.server.HttpServerExchange;
import java.io.IOException;
import java.util.concurrent.CompletableFuture;

public class HttpRequest {

  private final HttpServerExchange exchange;

  private final static ObjectMapper mapper = new ObjectMapper();

  HttpRequest(final HttpServerExchange exchange) {
    this.exchange = exchange;
  }

  public CompletableFuture<byte[]> body() {

    CompletableFuture<byte[]> completableFuture = new CompletableFuture<>();

    exchange
      .getRequestReceiver()
      .receiveFullBytes(
        (HttpServerExchange exchange, byte[] bytes) -> completableFuture.complete(bytes));

    return completableFuture;
  }

  public <T> CompletableFuture<T> json() {

    CompletableFuture<T> completableFuture = new CompletableFuture<>();

    TypeReference<T> typeRef = new TypeReference<T>() {
    };

    exchange
      .getRequestReceiver()
      .receiveFullBytes(
        (HttpServerExchange exchange, byte[] bytes) -> {
          try {
            completableFuture.complete(mapper.readValue(bytes, typeRef));
          } catch (IOException e) {
            throw new RuntimeException(e);
          }
        });

    return completableFuture;
  }
}
