package io.github.holmofy.data.jdbc.controller;

import io.github.holmofy.data.jdbc.dao.UserDao;
import io.github.holmofy.data.jdbc.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class UserController {

    @Autowired
    UserDao userDao;

    @GetMapping("/search")
    public Page<User> search(UserDao.UserQuery query, Pageable pageable) {
        return userDao.searchByQuery(query, pageable);
    }

    @GetMapping("/search-list1")
    public List<User> search1(UserDao.UserQuery query) {
        return userDao.searchByQuery1(query);
    }

    @GetMapping("/search-list2")
    public List<User> search2(UserDao.UserQuery query) {
        return userDao.searchByQuery2(query);
    }

}
