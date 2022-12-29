package io.github.holmofy.example;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.Transient;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

@Data
@Table("test_model")
public class TestModel {

    @Id
    private Long id;

    @Column("named_column")
    private String withColumn;

    private String lowerCamel;

    @Transient
    private String transientField;

    @Embedded(onEmpty = Embedded.OnEmpty.USE_EMPTY, prefix = "embedded_")
    private EmbeddedProperties embeddedField;

//    @Embedded.Empty
//    private EmptyEmbeddedProperties embeddedEmptyField;

    @Data
    public static class EmbeddedProperties {
        private String field1;
        @Column("field_2")
        private String field2;
    }

    @Data
    public static class EmptyEmbeddedProperties {
        private String field3;
        @Column("field_4")
        private String field4;
    }
}
