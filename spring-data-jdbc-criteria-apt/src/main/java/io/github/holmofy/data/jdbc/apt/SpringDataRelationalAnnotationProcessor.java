package io.github.holmofy.data.jdbc.apt;

import com.google.auto.service.AutoService;
import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.squareup.javapoet.ClassName;
import com.squareup.javapoet.FieldSpec;
import com.squareup.javapoet.JavaFile;
import com.squareup.javapoet.TypeSpec;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import javax.annotation.processing.*;
import javax.lang.model.SourceVersion;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@AutoService(Processor.class)
@SupportedOptions(APTOptions.show_log)
@SupportedSourceVersion(SourceVersion.RELEASE_17)
@SupportedAnnotationTypes("org.springframework.data.relational.core.mapping.Table")
public class SpringDataRelationalAnnotationProcessor extends AbstractProcessor {

    public static final Boolean ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS = Boolean.FALSE;

    private boolean shouldLogInfo;

    @Override
    @SneakyThrows
    public boolean process(Set<? extends TypeElement> annotations, RoundEnvironment roundEnv) {
        shouldLogInfo = processingEnv.getOptions().containsKey(APTOptions.show_log);
        if (roundEnv.processingOver() || annotations.size() == 0) {
            return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
        }

        if (roundEnv.getRootElements() == null || roundEnv.getRootElements().isEmpty()) {
            log(Diagnostic.Kind.NOTE, "No sources to process");
            return ALLOW_OTHER_PROCESSORS_TO_CLAIM_ANNOTATIONS;
        }
        Set<? extends Element> tables = roundEnv.getElementsAnnotatedWith(Table.class);

        for (Element table : tables) {
            if (table.getKind() != ElementKind.CLASS) {
                log(Diagnostic.Kind.ERROR, "@Activity should be on top of classes");
                continue;
            }
            TableModel model = analysisFields((TypeElement) table);
            JavaFile javaFile = model.generateSource();
            javaFile.writeTo(processingEnv.getFiler());
        }

        return true;
    }

    private TableModel analysisFields(TypeElement type) {
        TableModel tableModel = new TableModel();
        tableModel.setClassElement(type);
        tableModel.setPackageElement(processingEnv.getElementUtils().getPackageOf(type));
        tableModel.setFields(collectFields(type));
        return tableModel;
    }

    private List<VariableElement> collectFields(TypeElement type) {
        return processingEnv.getElementUtils()
                .getAllMembers(type)
                .stream()
                .map(this::filterFields)
                .filter(Objects::nonNull)
                .collect(Collectors.toList());
    }

    private VariableElement filterFields(Element field) {
        if (field.getKind() != ElementKind.FIELD) {
            return null;
        }
        VariableElement f = (VariableElement) field;
        if (f.getAnnotation(Transient.class) != null) {
            return null;
        }
        return f;
    }

    private void log(Diagnostic.Kind kind, String message) {
        if (shouldLogInfo) {
            processingEnv.getMessager().printMessage(kind, message);
        }
    }

    @Data
    public class TableModel {

        public static final String SPRING_DATA_PACKAGE = "org.springframework.data.relational.core.sql";

        private static final Converter<String, String> columnConverter = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);
        private static final Converter<String, String> tableConverter = CaseFormat.UPPER_CAMEL.converterTo(CaseFormat.LOWER_CAMEL);

        private PackageElement packageElement;

        private TypeElement classElement;

        private List<VariableElement> fields;

        public JavaFile generateSource() {
            TypeSpec.Builder builder = TypeSpec.classBuilder(getGenerateClassName())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            addFields(builder, this.fields, "");

            TypeSpec type = builder.build();
            return JavaFile.builder(packageElement.getQualifiedName().toString(), type)
                    .build();
        }

        private void addFields(TypeSpec.Builder builder, List<VariableElement> fields, String columnPrefix) {
            for (VariableElement field : fields) {
                Embedded embedded = field.getAnnotation(Embedded.class);
                Embedded.Empty embeddedEmpty = field.getAnnotation(Embedded.Empty.class);
                Embedded.Nullable embeddedNullable = field.getAnnotation(Embedded.Nullable.class);
                if (embedded == null && embeddedEmpty == null && embeddedNullable == null) {
                    addField(builder, field, columnPrefix);
                    continue;
                }
                String prefix = embedded != null ? embedded.prefix() :
                        embeddedEmpty != null ? embeddedEmpty.prefix() : embeddedNullable.prefix();
                TypeMirror typeMirror = field.asType();
                if (typeMirror.getKind().isPrimitive()) {
                    log(Diagnostic.Kind.WARNING, "@Embedded field is primitive");
                } else {
                    Element element = processingEnv.getTypeUtils().asElement(typeMirror);
                    if (element.getKind().isClass()) {
                        addFields(builder, collectFields((TypeElement) element), prefix);
                    }
                }
            }
        }

        private void addField(TypeSpec.Builder builder, VariableElement field, String columnPrefix) {
            String fieldName = field.getSimpleName().toString();
            String column_name = Optional.of(field).map(f -> f.getAnnotation(Column.class))
                    .map(Column::value)
                    .filter(s -> !s.isBlank())
                    .orElse(columnConverter.convert(fieldName));
            builder.addField(FieldSpec.builder(
                    ClassName.get("java.lang", "String"), fieldName, Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL
            ).initializer("$S", columnPrefix + column_name).build());
        }

        private String firstNonEmpty(String value, String name) {
            return value == null || value.isBlank() ? name : value;
        }

        public String getGenerateClassName() {
            return classElement.getSimpleName() + "_";
        }
    }

}