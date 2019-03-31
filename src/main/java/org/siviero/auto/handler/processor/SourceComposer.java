package org.siviero.auto.handler.processor;

import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.CodeBlock;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.MethodSpec;
import com.squareup.javapoet.ParameterSpec;
import com.squareup.javapoet.TypeName;
import com.squareup.javapoet.TypeSpec;
import io.undertow.Handlers;
import io.undertow.server.HttpHandler;
import io.undertow.server.HttpServerExchange;
import javax.inject.Inject;
import javax.lang.model.element.Modifier;
import org.siviero.auto.handler.HttpExchange;

class SourceComposer {

  static JavaFile generateSource(final GeneratedHandlerDescriptor descriptor) {
    final TypeSpec.Builder classBuilder = TypeSpec
      .classBuilder("AutoHandler" + descriptor.className());

    final MethodSpec.Builder constructorBuilder = MethodSpec
      .constructorBuilder()
      .addAnnotation(Inject.class);

    descriptor.endpoints().forEach(endpoint -> {
      final TypeName endpointType = ClassName.get(endpoint.type());
      final String endpointName = deCapitalize(endpoint.className());

      constructorBuilder
        .addParameter(ParameterSpec
          .builder(endpointType, endpointName)
          .build()
        )
        .addStatement(CodeBlock.of("this.$L = $L", endpointName, endpointName));

      classBuilder
        .addField(FieldSpec
          .builder(endpointType, endpointName, Modifier.FINAL, Modifier.PRIVATE)
          .build());

      final TypeSpec httpHandlerSpec = TypeSpec
        .anonymousClassBuilder("")
        .addSuperinterface(HttpHandler.class)
        .addMethod(MethodSpec.methodBuilder("handleRequest")
          .addAnnotation(Override.class)
          .addModifiers(Modifier.PUBLIC)
          .addParameter(HttpServerExchange.class, "exchange")
          .addStatement(CodeBlock
            .of("$T $L = $T.create($L)",
              HttpExchange.class,
              "requestExchange",
              HttpExchange.class,
              "exchange"))
          .addStatement(
            CodeBlock.of("$L.$L($L)", endpointName, endpoint.handlerName(), "requestExchange"))
          .build())
        .build();

      final MethodSpec.Builder handlerFactory = MethodSpec
        .methodBuilder("handler")
        .addModifiers(Modifier.PUBLIC)
        .returns(ClassName.get(HttpHandler.class))
        .addCode(CodeBlock.of("return $T.routing()", Handlers.class))
        .addCode(CodeBlock
          .of(".add($S, $S, $L)",
            endpoint.httpMethod(),
            endpoint.path(),
            httpHandlerSpec))
        .addCode(CodeBlock.of(";"));

      classBuilder.addMethod(handlerFactory.build());
    });

    classBuilder.addMethod(constructorBuilder.build());

    return JavaFile
      .builder(descriptor.handlerPackage(), classBuilder.build())
      .skipJavaLangImports(true)
      .build();
  }


  private static String deCapitalize(CharSequence text) {
    StringBuilder sb = new StringBuilder(text);
    sb.setCharAt(0, Character.toLowerCase(sb.charAt(0)));
    return sb.toString();
  }
}
