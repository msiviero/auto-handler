package org.siviero.auto.handler.processor;


import com.google.auto.service.AutoService;
import com.google.common.base.Throwables;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Iterables;
import com.google.googlejavaformat.java.filer.FormattingFiler;
import java.io.IOException;
import java.util.Set;
import java.util.function.Function;
import javax.annotation.processing.AbstractProcessor;
import javax.annotation.processing.Messager;
import javax.annotation.processing.ProcessingEnvironment;
import javax.annotation.processing.Processor;
import javax.annotation.processing.RoundEnvironment;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.TypeElement;
import javax.lang.model.util.Elements;
import javax.tools.Diagnostic.Kind;
import org.siviero.auto.handler.annotation.AutoHandler;
import org.siviero.auto.handler.annotation.Endpoint;
import org.siviero.auto.handler.annotation.Route;

@AutoService(Processor.class)
public class AutoHandlerProcessor extends AbstractProcessor {

  private Messager messager;
  private FormattingFiler filer;
  private Elements elements;

  @Override
  public synchronized void init(final ProcessingEnvironment processingEnvironment) {
    super.init(processingEnvironment);

    messager = processingEnvironment.getMessager();
    filer = new FormattingFiler(processingEnvironment.getFiler());
    elements = processingEnvironment.getElementUtils();
  }

  @Override
  public boolean process(
    final Set<? extends TypeElement> annotations,
    final RoundEnvironment round
  ) {
    try {
      process(round);
    } catch (Exception e) {
      messager.printMessage(Kind.ERROR, "AutoHandlerProcessor processor failed:\n" +
        Throwables.getStackTraceAsString(e));
    }
    return false;
  }

  private void process(final RoundEnvironment round) throws IOException {

    final Set<? extends Element> rootElements = round.getElementsAnnotatedWith(AutoHandler.class);

    if (rootElements.size() > 1) {
      this.messager.printMessage(Kind.ERROR, "You can only have @AutoHandler root");
      return;
    }

    final Element root = Iterables.getFirst(round
      .getElementsAnnotatedWith(AutoHandler.class), null);

    if (root == null) {
      return;
    }

    final String elementPackage = elements
      .getPackageOf(root)
      .getQualifiedName()
      .toString();

    final GeneratedHandlerDescriptor.Builder handlerDescriptor = GeneratedHandlerDescriptor
      .builder()
      .handlerPackage(elementPackage)
      .className(root.getSimpleName());

    round
      .getElementsAnnotatedWith(Endpoint.class)
      .stream()
      .map(endpointClass -> elements
        .getAllMembers((TypeElement) endpointClass)
        .stream()
        .filter(item -> item.getAnnotation(Route.class) != null)
        .map(methodClass -> EndpointDescriptor
          .builder()
          .className(endpointClass.getSimpleName())
          .type(endpointClass.asType())
          .httpMethod(methodClass.getAnnotation(Route.class).method())
          .handlerName(methodClass.getSimpleName())
          .path(endpointClass.getAnnotation(Endpoint.class).value()
            + methodClass.getAnnotation(Route.class).path())
          .build()
        )
      )
      .flatMap(Function.identity())
      .forEach(handlerDescriptor::addEndpoint);

    SourceComposer.generateSource(handlerDescriptor.build()).writeTo(filer);
  }


  @Override
  public Set<String> getSupportedAnnotationTypes() {
    return ImmutableSet.<String>builder()
      .add(AutoHandler.class.getCanonicalName())
      .add(Endpoint.class.getCanonicalName())
      .add(Route.class.getCanonicalName())
      .build();
  }

  @Override
  public SourceVersion getSupportedSourceVersion() {
    return SourceVersion.latestSupported();
  }
}
