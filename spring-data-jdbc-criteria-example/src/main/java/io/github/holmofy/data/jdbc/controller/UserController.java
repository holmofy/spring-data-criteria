package io.github.holmofy.data.jdbc.controller;

import io.github.holmofy.data.jdbc.dao.UserDao;
import io.github.holmofy.data.jdbc.model.User;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    UserDao userDao;

    @GetMapping("/search")
    public Page<User> search(UserDao.UserQuery query, Pageable pageable) {
        return userDao.searchByQuery(query, pageable);
    }

}
