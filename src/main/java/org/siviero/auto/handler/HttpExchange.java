package org.siviero.auto.handler;

import io.undertow.server.HttpServerExchange;

final public class HttpExchange {

  private final HttpServerExchange exchange;

  public static HttpExchange create(final HttpServerExchange exchange) {
    return new HttpExchange(exchange);
  }

  private HttpExchange(final HttpServerExchange exchange) {
    this.exchange = exchange;
  }

  public HttpRequest request() {
    return new HttpRequest(exchange);
  }
}
