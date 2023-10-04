package io.github.holmofy.data.jdbc.dao;

import io.github.holmofy.data.jdbc.CriteriaExecutor;
import io.github.holmofy.data.jdbc.JdbcSupport;
import io.github.holmofy.data.jdbc.model.User;
import io.github.holmofy.data.jdbc.model.User_;
import io.github.holmofy.data.relational.MoreCriteria;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.relational.core.query.Criteria;
import org.springframework.data.relational.core.sql.Conditions;
import org.springframework.data.relational.core.sql.SQL;
import org.springframework.data.relational.core.sql.Select;
import org.springframework.data.relational.core.sql.render.SqlRenderer;
import org.springframework.data.repository.ListCrudRepository;
import org.springframework.jdbc.core.namedparam.BeanPropertySqlParameterSource;

import java.time.LocalDateTime;
import java.util.List;

public interface UserDao extends ListCrudRepository<User, Long>, CriteriaExecutor<User>, JdbcSupport {

    default List<User> searchByQuery1(UserQuery query) {
        Select sql = Select.builder().select()
                .from(User_.TABLE)
                .where(Conditions.isEqual(User_.PROVINCE, SQL.bindMarker(":province")))
                .and(Conditions.isEqual(User_.CITY, SQL.bindMarker(":city")))
                .and(Conditions.isEqual(User_.AREA, SQL.bindMarker(":area")))
                .and(Conditions.isEqual(User_.NAME, SQL.bindMarker(":nick")))
                .build();
        return namedJdbcTemplate().queryForList(SqlRenderer.toString(sql)
                , new BeanPropertySqlParameterSource(query), User.class);
    }

    default List<User> searchByQuery2(UserQuery query) {
        return namedJdbcTemplate().queryForList(
                "select * " +
                        "from  t_user " +
                        "where 1=1 " +
                        (query.province == null ? "" : "and province=:province ") +
                        (query.city == null ? "" : "and city=:city ") +
                        (query.area == null ? "" : "and area=:area ") +
                        (query.nick == null ? "" : "and nick=:nick ")
                , new BeanPropertySqlParameterSource(query), User.class);
    }

    default Page<User> searchByQuery(UserQuery query, Pageable pageable) {
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
