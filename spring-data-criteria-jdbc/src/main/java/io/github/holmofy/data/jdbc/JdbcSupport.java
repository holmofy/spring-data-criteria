package io.github.holmofy.data.jdbc;

import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;

public interface JdbcSupport {

    JdbcOperations jdbcTemplate();

    NamedParameterJdbcOperations namedJdbcTemplate();

    JdbcAggregateOperations aggregateTemplate();

}
