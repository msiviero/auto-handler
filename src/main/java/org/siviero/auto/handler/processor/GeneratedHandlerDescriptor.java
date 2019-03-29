package org.siviero.auto.handler.processor;

import com.google.auto.value.AutoValue;
import com.google.common.collect.ImmutableSet;
import javax.lang.model.element.Name;

@AutoValue
abstract class GeneratedHandlerDescriptor {

  abstract String handlerPackage();

  abstract Name className();

  abstract ImmutableSet<EndpointDescriptor> endpoints();

  static Builder builder() {
    return new AutoValue_GeneratedHandlerDescriptor.Builder();
  }

  @AutoValue.Builder
  abstract static class Builder {

    abstract Builder handlerPackage(String value);

    abstract Builder className(Name value);

    abstract ImmutableSet.Builder<EndpointDescriptor> endpointsBuilder();

    Builder addEndpoint(EndpointDescriptor value) {
      endpointsBuilder().add(value);
      return this;
    }

    abstract GeneratedHandlerDescriptor build();
  }
}
