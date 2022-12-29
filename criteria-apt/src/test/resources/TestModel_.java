package io.github.holmofy.example;

import static org.springframework.data.relational.core.sql.SQL.*;

import org.springframework.data.relational.core.sql.Column;
import org.springframework.data.relational.core.sql.Table;

public final class TestModel_ {
    public static final Table table = table("test_model");

    public static final Column id = column("id", table);

    public static final Column named_column = column("named_column", table);

    public static final Column lower_camel = column("lower_camel", table);

    public static final Column field1 = column("embedded_field1",table);

    public static final Column field_2 = column("embedded_field_2",table);
}