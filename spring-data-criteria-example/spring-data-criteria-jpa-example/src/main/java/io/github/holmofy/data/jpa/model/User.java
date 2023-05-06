package io.github.holmofy.data.jpa.model;

import jakarta.persistence.*;
import lombok.Data;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;

@Data
@Entity
@Table(name = "t_user")
@EntityListeners(AuditingEntityListener.class)
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private Long id;

    @Column(name = "nick")
    private String name;

    @Embedded
    private Address address;

    @CreatedDate
    @Column(name = "created_date")
    private LocalDateTime created;

    @LastModifiedDate
    @Column(name = "last_modified_date")
    private LocalDateTime modified;

    @Data
    @Embeddable
    public static class Address {
        private String province;
        private String city;
        private String area;
    }

}
