package io.github.holmofy.data.apt;

import com.google.common.base.CaseFormat;
import com.google.common.base.Converter;
import com.squareup.javapoet.*;
import lombok.Data;
import lombok.SneakyThrows;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;
import org.springframework.util.StringUtils;

import javax.annotation.processing.Generated;
import javax.annotation.processing.ProcessingEnvironment;
import javax.lang.model.element.*;
import javax.lang.model.type.TypeMirror;
import javax.tools.Diagnostic;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

public class CriteriaGenerator {

    private final LogHelper logger;
    private final ProcessingEnvironment processingEnv;

    public CriteriaGenerator(LogHelper logger, ProcessingEnvironment processingEnv) {
        this.logger = logger;
        this.processingEnv = processingEnv;
    }

    @SneakyThrows
    public void generateSource(TypeElement table) {
        TableModel model = analysisFields(table);
        JavaFile javaFile = model.generateSource();
        javaFile.writeTo(processingEnv.getFiler());
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
        if (f.getModifiers().contains(Modifier.STATIC)) {
            return null;
        }
        if (f.getAnnotation(Transient.class) != null) {
            return null;
        }
        return f;
    }

    @Data
    public class TableModel {

        public static final String SPRING_DATA_PACKAGE = "org.springframework.data.relational.core.sql";

        private static final Converter<String, String> columnConverter = CaseFormat.LOWER_CAMEL.converterTo(CaseFormat.LOWER_UNDERSCORE);

        private PackageElement packageElement;

        private TypeElement classElement;

        private List<VariableElement> fields;

        public JavaFile generateSource() {
            TypeSpec.Builder builder = TypeSpec.classBuilder(getGenerateClassName())
                    .addModifiers(Modifier.PUBLIC, Modifier.FINAL);

            /**
             * @Generated(value = "io.github.holmofy.data.apt.SpringDataRelationalAnnotationProcessor")
             */
            builder.addAnnotation(AnnotationSpec.builder(Generated.class)
                    .addMember("value", "$S", SpringDataRelationalAnnotationProcessor.class.getCanonicalName())
                    .build()
            );

            // table name
            String tableName = getTableName();
            builder.addField(FieldSpec.builder(
                    ClassName.get("java.lang", "String"), "TABLE", Modifier.PUBLIC, Modifier.STATIC, Modifier.FINAL
            ).initializer("$S", tableName).build());

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
                    logger.log(Diagnostic.Kind.WARNING, "@Embedded field is primitive");
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

        public String getGenerateClassName() {
            return classElement.getSimpleName() + "_";
        }

        private String getTableName() {
            Table annotation = classElement.getAnnotation(Table.class);
            if (annotation != null) {
                if (StringUtils.hasLength(annotation.name())) {
                    return annotation.name();
                }
                if (StringUtils.hasLength(annotation.value())) {
                    return annotation.value();
                }
            }
            return columnConverter.convert(classElement.getSimpleName().toString());
        }
    }

}
