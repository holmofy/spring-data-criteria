[![Build Status(https://github.com/holmofy/spring-data-jdbc-criteria/actions/workflows/package.yaml/badge.svg)](https://github.com/holmofy/spring-data-jdbc-criteria/actions/workflows/package.yaml/badge.svg)](https://repo1.maven.org/maven2/io/github/holmofy/)

`Spring Data Jdbc Criteria` extends Spring Data JDBC to support dynamic sql. to see [`DATAJDBC-319`](https://github.com/spring-projects/spring-data-relational/issues/542). The usage is as follows:

```java
default Page<User> searchByQuery(UserQuery query, Pageable pageable) {
    return findAll(Criteria.from(eq(User_.province, query.province))
                    .and(eq(User_.city, query.city))
                    .and(like(User_.area, query.area))
                    .and(like(User_.name, query.nick))
                    .and(between(User_.created, query.createFrom, query.createTo))
            , pageable);
}
```

It will dynamically generate sql based on whether the query field is empty.

## How to use 

1、add dependency
```xml
<dependency>
    <groupId>io.github.holmofy</groupId>
    <artifactId>spring-data-jdbc-criteria-core</artifactId>
    <version>3.0.0</version>
</dependency>
```

2、add apt plugin. It is like `hibernate-jpamodelgen`, which will help you generate corresponding mapping fields according to the model of SpringDataJdbc
```xml
<build>
    <plugins>
        <plugin>
            <groupId>org.apache.maven.plugins</groupId>
            <artifactId>maven-compiler-plugin</artifactId>
            <version>${maven-compiler-plugin.version}</version>
            <configuration>
                <source>${maven.compiler.source}</source>
                <target>${maven.compiler.target}</target>
                <annotationProcessorPaths>
                    <path>
                        <groupId>io.github.holmofy</groupId>
                        <artifactId>spring-data-jdbc-criteria-apt</artifactId>
                        <version>3.0.0</version>
                    </path>
                    <!-- other apt plugin -->
                    <path>
                        <groupId>org.projectlombok</groupId>
                        <artifactId>lombok</artifactId>
                        <version>1.18.24</version>
                    </path>
                </annotationProcessorPaths>
            </configuration>
        </plugin>
    </plugins>
</build>
```

3、config Spring Data Jdbc: `@EnableJdbcRepositories(repositoryBaseClass = EnhancedJdbcRepository.class)`

```java
@SpringBootApplication
@EnableJdbcRepositories(repositoryBaseClass = EnhancedJdbcRepository.class)
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

}
```

4、use dynamic sql

1)、define model
```java
@Data
@Table("t_user")
public class User {

    @Id
    private Long id;

    @Column("nick")
    private String name;

    @Embedded.Empty
    private Address address;

    @CreatedDate
    @Column("create_date")
    private LocalDateTime created;

    @LastModifiedDate
    @Column("last_modified_date")
    private LocalDateTime modified;

    @Data
    public static class Address {
        private String province;
        private String city;
        private String area;
    }

}
```
2)、Dao interface
```java
public interface UserDao extends ListCrudRepository<User, Long>, CriteriaExecutor<User> {

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
```
3)、use Dao
```java
@RestController
@AllArgsConstructor(onConstructor_ = @Autowired)
public class UserController {

    UserDao userDao;

    @GetMapping("/search")
    public Page<User> search(UserDao.UserQuery query, Pageable pageable) {
        return userDao.searchByQuery(query, pageable);
    }

}
```

The complete example is [here](https://github.com/holmofy/spring-data-jdbc-criteria/tree/master/spring-data-jdbc-criteria-example).

If you think it's good, please give this project a star.
