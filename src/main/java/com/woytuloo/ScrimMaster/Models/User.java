package com.woytuloo.ScrimMaster.Models;

import jakarta.persistence.*;
import lombok.Data;


@Data
@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;

    @Column(unique = true, nullable = false)
    private String username;

    @Column(nullable = false)
    private String password;



    @Column(unique = true, nullable = false)
    private String email;
    private double kd;
    private double adr;
    private int ranking;

    private String role = "ROLE_USER";

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;

    }



    public User() { }

}
