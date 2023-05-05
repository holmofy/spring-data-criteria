package io.github.holmofy.data.r2dbc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.convert.R2dbcConverter;
import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.r2dbc.core.ReactiveDataAccessStrategy;
import org.springframework.data.r2dbc.repository.support.SimpleR2dbcRepository;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import org.springframework.data.relational.core.query.Query;
import org.springframework.data.relational.repository.query.RelationalEntityInformation;
import org.springframework.r2dbc.core.DatabaseClient;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;

public class EnhancedR2dbcRepository<T, ID> extends SimpleR2dbcRepository<T, ID> implements CriteriaExecutor<T>, R2dbcSupport {

    private final RelationalEntityInformation<T, ID> entity;
    private final R2dbcEntityOperations entityOperations;

    public EnhancedR2dbcRepository(RelationalEntityInformation<T, ID> entity,
                                   R2dbcEntityOperations entityOperations,
                                   R2dbcConverter converter) {
        super(entity, entityOperations, converter);
        this.entity = entity;
        this.entityOperations = entityOperations;
    }

    public EnhancedR2dbcRepository(RelationalEntityInformation<T, ID> entity,
                                   DatabaseClient databaseClient,
                                   R2dbcConverter converter,
                                   ReactiveDataAccessStrategy accessStrategy) {
        this(entity, new R2dbcEntityTemplate(databaseClient, accessStrategy), converter);
    }

    @Override
    public Mono<T> findOne(CriteriaDefinition criteria) {
        return entityOperations.selectOne(Query.query(criteria), entity.getJavaType());
    }

    @Override
    public Flux<T> findAll(CriteriaDefinition criteria) {
        return entityOperations.select(Query.query(criteria), entity.getJavaType());
    }

    @Override
    public Mono<Long> count(CriteriaDefinition criteria) {
        return entityOperations.count(Query.query(criteria), entity.getJavaType());
    }

    @Override
    public Mono<Page<T>> findAll(CriteriaDefinition criteria, Pageable pageable) {
        Mono<Long> count = count(criteria);
        Mono<List<T>> list = entityOperations.select(Query.query(criteria).with(pageable), entity.getJavaType())
                .collectList();
        return Mono.zip(list, count).map(tuple -> new PageImpl<>(tuple.getT1(), pageable, tuple.getT2()));
    }

    @Override
    public R2dbcEntityOperations r2dbcTemplate() {
        return entityOperations;
    }
}
