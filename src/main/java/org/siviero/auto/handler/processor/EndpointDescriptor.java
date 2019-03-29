package org.siviero.auto.handler.processor;

import com.google.auto.value.AutoValue;
import javax.lang.model.element.Name;
import javax.lang.model.type.TypeMirror;
import org.siviero.auto.handler.HttpMethod;

@AutoValue
abstract class EndpointDescriptor {

  abstract TypeMirror type();

  abstract Name className();

  abstract HttpMethod httpMethod();

  abstract Name handlerName();

  abstract String path();

  static EndpointDescriptor.Builder builder() {
    return new AutoValue_EndpointDescriptor.Builder();
  }

  @AutoValue.Builder
  abstract static class Builder {

    abstract Builder type(TypeMirror value);

    abstract Builder className(Name value);

    abstract Builder httpMethod(HttpMethod value);

    abstract Builder handlerName(Name value);

    abstract Builder path(String path);

    abstract EndpointDescriptor build();
  }
}
