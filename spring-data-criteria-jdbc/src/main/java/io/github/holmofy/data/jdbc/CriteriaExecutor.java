package io.github.holmofy.data.jdbc;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.CriteriaDefinition;

import java.util.Optional;

/**
 * Support Criteria queries
 *
 * @param <T> Domain Class
 */
public interface CriteriaExecutor<T> {

    Optional<T> findOne(CriteriaDefinition criteria);

    Iterable<T> findAll(CriteriaDefinition criteria);

    Page<T> findAll(CriteriaDefinition criteria, Pageable pageable);

}
