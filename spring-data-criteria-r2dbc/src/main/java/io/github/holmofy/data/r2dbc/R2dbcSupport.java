package io.github.holmofy.data.r2dbc;

import org.springframework.data.r2dbc.core.R2dbcEntityOperations;
import org.springframework.data.r2dbc.core.R2dbcEntityTemplate;

public interface R2dbcSupport {

    /**
     * @return R2dbcEntityTemplate
     * @see R2dbcEntityTemplate
     */
    R2dbcEntityOperations r2dbcTemplate();

}
