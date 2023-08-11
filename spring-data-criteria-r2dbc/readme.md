## How to use

1、add dependency
```xml
<dependency>
    <groupId>io.github.holmofy</groupId>
    <artifactId>spring-data-criteria-r2dbc</artifactId>
    <version>${latest-version}</version>
</dependency>
```

Find the latest version [here](https://repo1.maven.org/maven2/io/github/holmofy/spring-data-criteria-r2dbc)

2、add apt plugin. It is like `hibernate-jpamodelgen`, which will help you generate corresponding mapping fields according to the model of SpringData
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
                        <artifactId>spring-data-criteria-apt</artifactId>
                        <version>${latest-version}</version>
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

3、config Spring Data R2DBC: `@EnableR2dbcRepositories(repositoryBaseClass = EnhancedR2dbcRepository.class)`

```java
@SpringBootApplication
@EnableR2dbcRepositories(repositoryBaseClass = EnhancedR2dbcRepository.class)
public class MainApplication {

    public static void main(String[] args) {
        SpringApplication.run(MainApplication.class, args);
    }

    // Webflux does not support Pageable by default, so we need to manually register it.
    @Bean
    WebFluxConfigurer springDataArgumentResolversConfigurer() {
        return new WebFluxConfigurer() {

            @Override
            public void configureArgumentResolvers(ArgumentResolverConfigurer configurer) {
                configurer.addCustomResolver(new ReactiveSortHandlerMethodArgumentResolver(),
                        new ReactivePageableHandlerMethodArgumentResolver());
            }
        };
    }

}
```

**NOTE: These two issues [spring-data-commons/issues/1846](https://github.com/spring-projects/spring-data-commons/issues/1846) & [spring-boot/issues/20701](https://github.com/spring-projects/spring-boot/issues/20701) can track the progress of WebFlux's support for `Pageable`**

4、use dynamic sql

1)、define model

**NOTE: spring-data-r2dbc currently does not support @Embedded to see [spring-data-r2dbc/issues/288](https://github.com/spring-projects/spring-data-r2dbc/issues/288)**

```java
@Data
@Table("t_user")
public class User {

    @Id
    private Long id;

    @Column("nick")
    private String name;

    // NOTE: spring-data-r2dbc currently does not support @Embedded 
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
public interface UserDao extends ListCrudRepository<User, Long>, 
              CriteriaExecutor<User>, JdbcSupport {

    /**
     * Criteria example
     */
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
    public Memo<Page<User>> search(UserDao.UserQuery query, Pageable pageable) {
        return userDao.searchByQuery(query, pageable);
    }

}
```

The complete example is [here](../spring-data-criteria-example/spring-data-criteria-r2dbc-example).

If you think it's good, please give this project a star.
