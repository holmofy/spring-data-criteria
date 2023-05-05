package io.github.holmofy.data.r2dbc.controller;

import io.github.holmofy.data.r2dbc.dao.UserDao;
import io.github.holmofy.data.r2dbc.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;

@RestController
public class UserController {

    @Autowired
    UserDao userDao;

    @GetMapping("/search")
    public Mono<Page<User>> search(UserDao.UserQuery query, Pageable pageable) {
        return userDao.searchByQuery(query, pageable);
    }

}
