package io.github.holmofy.example;

import java.lang.String;
import javax.annotation.processing.Generated;
import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.SQL;
import org.springframework.data.relational.core.sql.Table;

@Generated("io.github.holmofy.data.apt.SpringDataRelationalAnnotationProcessor")
public final class TestModel_ {
    public static final String TABLE_NAME = "test_model";
    public static final Table TABLE = SQL.table(TABLE_NAME);

    public static final String id = "id";
    public static final Column ID = SQL.column(id, TABLE);

    public static final String withColumn = "named_column";
    public static final Column WITH_COLUMN = SQL.column(withColumn, TABLE);

    public static final String lowerCamel = "lower_camel";
    public static final Column LOWER_CAMEL = SQL.column(lowerCamel, TABLE);

    public static final String field1 = "embedded_field1";
    public static final Column FIELD1 = SQL.column(field1, TABLE);

    public static final String field2 = "embedded_field_2";
    public static final Column FIELD2 = SQL.column(field2, TABLE);
}