package io.github.holmofy.data.jdbc;

import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.JdbcAggregateTemplate;
import org.springframework.jdbc.core.JdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcOperations;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class JdbcSupportImpl implements JdbcSupport {

    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;
    private final JdbcAggregateTemplate jdbcAggregateTemplate;

    public JdbcSupportImpl(NamedParameterJdbcTemplate namedParameterJdbcTemplate,
                           JdbcAggregateTemplate jdbcAggregateTemplate) {
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
        this.jdbcAggregateTemplate = jdbcAggregateTemplate;
    }

    @Override
    public JdbcOperations jdbcTemplate() {
        return namedParameterJdbcTemplate.getJdbcOperations();
    }

    @Override
    public NamedParameterJdbcOperations namedJdbcTemplate() {
        return namedParameterJdbcTemplate;
    }

    @Override
    public JdbcAggregateOperations aggregateTemplate() {
        return jdbcAggregateTemplate;
    }

}