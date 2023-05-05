package io.github.holmofy.data.r2dbc.dao;

import io.github.holmofy.data.r2dbc.CriteriaExecutor;
import io.github.holmofy.data.r2dbc.model.User;
import io.github.holmofy.data.r2dbc.model.User_;
import io.github.holmofy.data.relational.MoreCriteria;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.repository.ListCrudRepository;
import reactor.core.publisher.Mono;

import java.time.LocalDateTime;

public interface UserDao extends ListCrudRepository<User, Long>, CriteriaExecutor<User> {

    default Mono<Page<User>> searchByQuery(UserQuery query, Pageable pageable) {
        return findAll(Criteria.from(MoreCriteria.eq(User_.province, query.province))
                        .and(MoreCriteria.eq(User_.city, query.city))
                        .and(MoreCriteria.like(User_.area, query.area))
                        .and(MoreCriteria.like(User_.name, query.nick))
                        .and(MoreCriteria.between(User_.created, query.createFrom, query.createTo))
                , pageable);
    }

    @Data
    class UserQuery {
        String nick;
        String province;
        String city;
        String area;
        LocalDateTime createFrom;
        LocalDateTime createTo;
    }

}
