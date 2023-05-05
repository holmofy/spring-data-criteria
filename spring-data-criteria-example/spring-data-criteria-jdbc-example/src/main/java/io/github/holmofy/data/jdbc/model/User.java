package io.github.holmofy.data.jdbc.model;

import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Embedded;
import org.springframework.data.relational.core.mapping.Table;

import java.time.LocalDateTime;

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
    @Column("created_date")
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
