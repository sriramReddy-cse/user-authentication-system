package com.book.user;

import com.book.bookEntity.Book;
import com.book.history.BookTransactionHistory;
import com.book.role.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import javax.security.auth.Subject;
import java.security.Principal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Collectors;

@Getter
@Setter
@Entity
@Table(name = "_user") //this _user is created because by default in postgresql the user keyword is avilable
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)//this entity listener is used to track the changes made to the DB like who created,deleted,createddate..etc
@Builder //this annotation is used to build by using the . extension and end with .build()
@AllArgsConstructor
public class User implements UserDetails, Principal{

    @Id //it is a primary key for my user entity
    @GeneratedValue
    private Integer id;
    private String firstname;
    private String lastname;
    @Column(unique = true)
    private String email;
    private String password;
    private LocalDate dateOfBirth;
    private boolean accountLocked;
    private boolean enabled;

    @ManyToMany(fetch = FetchType.EAGER)
    @JsonIgnore
    private List<Role> roles;

    //we know that a user can have multiple books

    @OneToMany(mappedBy = "owner",fetch = FetchType.EAGER)
    private List<Book>books;


    @OneToMany(mappedBy = "user")
    private List<BookTransactionHistory>bookTransactionHistories;

    @CreatedDate
    @Column(updatable = false,nullable = false)
    private LocalDateTime createdDate;

    @Column(insertable = false)
    private LocalDateTime lastModifiedDate;

    @Override
    public String getName() {
        return email;
    }

    public String fullname(){
        return firstname +" "+lastname;
    }


    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return this.roles.stream().map(r -> new SimpleGrantedAuthority(r.getName()))
                .collect(Collectors.toList());
    }

    @Override
    public boolean implies(Subject subject) {
        return Principal.super.implies(subject);
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return this.email;
    }

    @Override
    public boolean isAccountNonExpired() {
        return true;
    }

    @Override
    public boolean isAccountNonLocked() {
        return !accountLocked;
    }

    @Override
    public boolean isCredentialsNonExpired() {
        return true;
    }

    @Override
    public boolean isEnabled() {
        return enabled;
    }
}
