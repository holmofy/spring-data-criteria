package io.github.holmofy.data.jdbc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jdbc.core.JdbcAggregateOperations;
import org.springframework.data.jdbc.core.convert.JdbcConverter;
import org.springframework.data.jdbc.repository.support.SimpleJdbcRepository;
import org.springframework.data.mapping.PersistentEntity;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import org.springframework.data.relational.core.query.Query;

import java.util.Optional;

public class EnhancedJdbcRepository<T, ID> extends SimpleJdbcRepository<T, ID> implements CriteriaExecutor<T> {

    private final JdbcAggregateOperations entityOperations;
    private final PersistentEntity<T, ?> entity;

    public EnhancedJdbcRepository(JdbcAggregateOperations entityOperations,
                                  PersistentEntity<T, ?> entity,
                                  JdbcConverter converter) {
        super(entityOperations, entity, converter);
        this.entityOperations = entityOperations;
        this.entity = entity;
    }

    @Override
    public Optional<T> findOne(CriteriaDefinition criteria) {
        return entityOperations.findOne(Query.query(criteria), entity.getType());
    }

    @Override
    public Iterable<T> findAll(CriteriaDefinition criteria) {
        return entityOperations.findAll(Query.query(criteria), entity.getType());
    }

    @Override
    public Page<T> findAll(CriteriaDefinition criteria, Pageable pageable) {
        return entityOperations.findAll(Query.query(criteria), entity.getType(), pageable);
    }
}
