package io.github.holmofy.data.r2dbc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;
import org.springframework.data.relational.core.query.CriteriaDefinition;
import org.springframework.data.relational.core.query.Query;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

/**
 * Support Criteria queries
 *
 * @param <T> Domain Class
 */
public interface CriteriaExecutor<T> {

    /**
     * @see R2dbcEntityTemplate#selectOne(Query, Class)
     */
    Mono<T> findOne(CriteriaDefinition criteria);

    /**
     * @see R2dbcEntityTemplate#select(Query, Class)
     */
    Flux<T> findAll(CriteriaDefinition criteria);

    /**
     * @see R2dbcEntityTemplate#count(Query, Class)
     */
    Mono<Long> count(CriteriaDefinition criteria);

    /**
     * @see R2dbcEntityTemplate#select(Query, Class)
     */
    Mono<Page<T>> findAll(CriteriaDefinition criteria, Pageable pageable);

}
