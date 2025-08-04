package com.book.role;


import com.book.user.User;
import jakarta.persistence.*;
import jdk.dynalink.linker.LinkerServices;
import lombok.*;
import org.springframework.context.event.EventListener;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Entity
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)//this entity listener is used to track the changes made to the DB like who created,deleted,createddate..etc
@Builder //this annotation is used to build by using the . extension and end with .build()
@AllArgsConstructor
public class Role {

    @Id
    @GeneratedValue
    private Integer id;

    @Column(unique = true)
    private String name;

    @Column(nullable = false,updatable = false)
    private LocalDateTime createdDate;
    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    @ManyToMany(mappedBy = "roles")
    private List<User> users;

}
