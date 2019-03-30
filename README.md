# Undertow and Dagger bindings generated at compile time

## install

Add auto-handler in build.gradle

```grrovy

compile files('./auto-handler-0.0.1-all.jar')

annotationProcessor files('./auto-handler-0.0.1-all.jar')
```
## usage

Create an endpoint:

```java
@AutoHandler
@Singleton
@Endpoint("/api/v1")
public final class MainEndpoint {

  private final SampleService service;

  @Inject
  MainEndpoint(SampleService service) {
    this.service = service;
  }


  @Route(method = HttpMethod.GET, path = "/greet")
  public void greet(HttpServerExchange exchange) {
    exchange.getResponseSender().send("Hello " + service.name() + "!!!");
  }
}
```

Create a root annotated java type, e.g.:

```java
@AutoHandler
interface Root {}
```

auto-handler will generate a class using your root @AutoHandler annotated type which is:

- A valid dagger injectable class
- A undertow handler implementing your endpoints

The class name will be the same as yours, prefixed with AutoHandler.

Now you can add your handler to your dagger graph:

```java
@Component
public interface ApplicationGraph {

  AutoHandlerRoot autoHandlerRoot();
}
```

And to undertow server:

```java
class Application {

  void run() {

    final AutoHandlerRoot autoHandler = DaggerApplicationGraph.create().autoHandlerRoot();

    Undertow
      .builder()
      .addHttpListener(8000, "0.0.0.0")
      .setHandler(autoHandler.handler())
      .build()
      .start();
  }
}

