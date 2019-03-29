package org.siviero.auto.handler.annotation;

import static java.lang.annotation.RetentionPolicy.SOURCE;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.Target;
import org.siviero.auto.handler.HttpMethod;

@Retention(SOURCE)
@Target(ElementType.METHOD)
public @interface Route {

  HttpMethod method() default HttpMethod.GET;

  String path() default "/";
}
