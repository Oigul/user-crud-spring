package com.example.user_crud_spring.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Table(name = "users")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false, length = 255)
    private String name;

    @Column(name = "email", nullable = false, unique = true)
    private String email;

    @Column(name = "age", nullable = false)
    private Integer age;

    @Column(name = "created_at", nullable = false, updatable = false)
    private java.time.Instant createdAt;

    public User(String name, String email, Integer age) {
        this.name = name;
        this.email = email;
        this.age = age;
    }

    @PrePersist
    protected void onCreate() {
        this.createdAt = java.time.Instant.now();
    }

    @Override
    public String toString() {
        return  id +
                " " + name +
                " " + email +
                " " + age;
    }
}
