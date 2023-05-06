## How to use

1、add dependency
```xml
<dependency>
    <groupId>io.github.holmofy</groupId>
    <artifactId>spring-data-criteria-jpa</artifactId>
    <version>${latest-version}</version>
</dependency>
```

Find the latest version [here](https://repo1.maven.org/maven2/io/github/holmofy/)

2、add apt plugin: `hibernate-jpamodelgen`
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
                        <groupId>org.hibernate.orm</groupId>
                        <artifactId>hibernate-jpamodelgen</artifactId>
                        <version>6.1.7.Final</version>
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

3、config Spring Data JPA

```java
@EnableJpaAuditing
@EnableJpaRepositories
@SpringBootApplication
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
@Entity
@Table(name = "t_user")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "user_seq")
    @SequenceGenerator(name = "user_seq", sequenceName = "t_user_seq", allocationSize = 1)
    private Long id;

    @Column(name = "nick")
    private String name;

    // NOTE: jpamodelgen currently does not support @Embedded
    private String province;
    private String city;
    private String area;

    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime created;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime modified;


}
```
2)、Dao interface
```java
public interface UserDao extends JpaRepository<User, Long>, JpaSpecificationExecutor<User> {

    default Page<User> searchByQuery(UserQuery query, Pageable pageable) {
        return findAll(Specification.where(MoreCriteria.eq(User_.province, query.province))
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
public class UserController {

    @Autowired
    UserDao userDao;

    @GetMapping("/search")
    public Page<User> search(UserDao.UserQuery query, Pageable pageable) {
        return userDao.searchByQuery(query, pageable);
    }

}
```

The complete example is [here](../spring-data-criteria-example/spring-data-criteria-jpa-example).

If you think it's good, please give this project a star.
