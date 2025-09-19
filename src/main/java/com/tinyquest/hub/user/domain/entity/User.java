package com.tinyquest.hub.user.domain.entity;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable=false, length=100, unique = true)
    private String email;

    @Column(nullable=false, length=50)
    private String name;

    private Integer age;

    @Column(nullable=false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    protected User() {}
    private User(String email, String name, Integer age) {
        this.email = email; this.name = name; this.age = age;
    }
    public static User of(String email, String name, Integer age){ return new User(email,name,age); }

    public Long getId(){return id;}
    public String getEmail(){return email;}
    public String getName(){return name;}
    public Integer getAge(){return age;}
    public LocalDateTime getCreatedAt(){return createdAt;}

    public void change(String name, Integer age){ this.name = name; this.age = age; }
}
