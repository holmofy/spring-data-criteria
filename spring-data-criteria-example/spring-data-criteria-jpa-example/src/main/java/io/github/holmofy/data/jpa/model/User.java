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
