package io.github.holmofy.data.jpa.dao;

import io.github.holmofy.data.jpa.MoreCriteria;
import io.github.holmofy.data.jpa.model.User;
import io.github.holmofy.data.jpa.model.User_;
import lombok.Data;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

import java.time.LocalDateTime;

public interface UserDao extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    default Page<User> searchByQuery(UserQuery query, Pageable pageable) {
        return findAll(Specification.where(MoreCriteria.eq(User_.address, query.province))
//                        .and(MoreCriteria.eq(User_.city, query.city))
//                        .and(MoreCriteria.like(User_.area, query.area))
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
