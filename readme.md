[![Build Status(https://github.com/holmofy/spring-data-jdbc-criteria/actions/workflows/package.yaml/badge.svg)](https://github.com/holmofy/spring-data-jdbc-criteria/actions/workflows/package.yaml/badge.svg)](https://repo1.maven.org/maven2/io/github/holmofy/)

`Spring Data Criteria` extends Spring Data JDBC & Spring Data R2DBC & Spring Data JPA to support dynamic sql. 

to see [`DATAJDBC-319`](https://github.com/spring-projects/spring-data-relational/issues/542), [`DATAJPA-2724`](https://github.com/spring-projects/spring-data-jpa/issues/2724).

The usage is as follows:

```java
public interface UserDao extends ListCrudRepository<User, Long>, CriteriaExecutor<User> {
    default Page<User> searchByQuery(UserQuery query, Pageable pageable) {
        return findAll(Criteria.from(eq(User_.province, query.province))
                        .and(eq(User_.city, query.city))
                        .and(like(User_.area, query.area))
                        .and(like(User_.name, query.nick))
                        .and(between(User_.created, query.createFrom, query.createTo))
                , pageable);
    }
}
```

It will dynamically generate sql based on whether the query field is empty.

## How to use 

* [Using it with `spring-data-jdbc`](./spring-data-criteria-jdbc)
* [Using it with `spring-data-jpa`](./spring-data-criteria-jpa)
* [Using it with `spring-data-r2dbc`](./spring-data-criteria-r2dbc)

