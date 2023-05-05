package io.github.holmofy.data.apt;

import com.google.auto.service.AutoService;
import lombok.SneakyThrows;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.Element;
import javax.lang.model.element.ElementKind;
import javax.lang.model.element.TypeElement;
import javax.tools.Diagnostic;
import java.util.Set;

@AutoService(Processor.class)
@SupportedOptions(APTOptions.show_log)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("org.springframework.data.relational.core.mapping.Table")
public class SpringDataRelationalAnnotationProcessor extends AbstractProcessor {

    public static final Boolean ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS = Boolean.FALSE;

    @Override
    @SneakyThrows
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        LogHelper logger = new LogHelper(processingEnv);
        if (roundEnv.processingOver() || annotations.size() == 0) {
            return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
        }

        if (roundEnv.getRootElements() == null || roundEnv.getRootElements().isEmpty()) {
            logger.log(Diagnostic.Kind.NOTE, "No sources to process");
            return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
        }

        Set<? extends Element> tables = roundEnv.getElementsAnnotatedWith(Table.class);
        CriteriaGenerator criteriaGenerator = new CriteriaGenerator(logger, processingEnv);
        for (Element table : tables) {
            if (table.getKind() != ElementKind.CLASS) {
                logger.log(Diagnostic.Kind.ERROR, "@Table should be placed at the class level");
                continue;
            }
            criteriaGenerator.generateSource((TypeElement) table);
        }

        return true;
    }

}