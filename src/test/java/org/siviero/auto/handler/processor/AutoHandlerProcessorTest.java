package org.siviero.auto.handler.processor;


import static com.google.testing.compile.JavaSourcesSubject.assertThat;

import com.google.testing.compile.JavaFileObjects;
import javax.tools.JavaFileObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

@RunWith(JUnit4.class)
public class AutoHandlerProcessorTest {

  @Test
  public void testProcessor() {

    JavaFileObject autoHandlerRoot = JavaFileObjects.forSourceLines(
      "com.example.handler.Root",
      "package com.example.handler;",
      "",
      "import org.siviero.auto.handler.annotation.AutoHandler;",
      "",
      "@AutoHandler",
      "interface Root {",
      "}"
    );

    JavaFileObject endpoint = JavaFileObjects.forSourceLines(
      "com.example.addEndpoint.UsersApi",
      "package com.example.addEndpoint;",
      "",
      "import org.siviero.auto.handler.annotation.Endpoint;",
      "import org.siviero.auto.handler.annotation.Route;",
      "import org.siviero.auto.handler.HttpMethod;",
      "import io.undertow.server.HttpServerExchange;",
      "",
      "@Endpoint(\"/users\")",
      "public class UsersApi {",
      "",
      "  @Route(method=HttpMethod.GET, path=\"/all\")",
      "  public void list(HttpServerExchange exchange) { }",
      "}"
    );

    JavaFileObject generatedRoot = JavaFileObjects.forSourceLines(
      "com.example.handler.AutoHandlerRoot",
      "package com.example.handler;",
      "",
      "import com.example.addEndpoint.UsersApi;",
      "import io.undertow.Handlers;",
      "import io.undertow.server.HttpHandler;",
      "import io.undertow.server.HttpServerExchange;",
      "import javax.inject.Inject;",
      "",
      "class AutoHandlerRoot {",
      "",
      "private final UsersApi usersApi;",
      "",
      "  @Inject",
      "  AutoHandlerRoot(",
      "    UsersApi usersApi",
      "  ) {",
      "    this.usersApi=usersApi;",
      "  }",
      "",
      "  public HttpHandler handler() {",
      "    return Handlers.routing()",
      "      .add(\"GET\", \"/users/all\", new HttpHandler() {",
      "         @Override",
      "         public void handleRequest(HttpServerExchange exchange) {",
      "           usersApi.list(exchange);",
      "         }",
      "      })",
      "    ;",
      "  }",
      "",
      "}",
      ""
    );

    assertThat(autoHandlerRoot, endpoint)
      .processedWith(new AutoHandlerProcessor())
      .compilesWithoutError()
      .and()
      .generatesSources(
        generatedRoot
      );
  }
}