package com.woytuloo.ScrimMaster.Models;

import jakarta.persistence.*;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long id;
    private String username;
    private String password;
    private String email;
    private double kd;
    private double adr;
    private int ranking;
    private long teamId;
    private Integer persmissionLevel = 0;

    public User(String username, String password, String email) {
        this.username = username;
        this.password = password;
        this.email = email;
    }

    public User() { }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public Integer getPersmissionLevel() {
        return persmissionLevel;
    }

    public void setPersmissionLevel(Integer persmissionLevel) {
        this.persmissionLevel = persmissionLevel;
    }

    public long getTeamId() {
        return teamId;
    }

    public void setTeamId(long teamId) {
        this.teamId = teamId;
    }

    public int getRanking() {
        return ranking;
    }

    public void setRanking(int ranking) {
        this.ranking = ranking;
    }

    public double getAdr() {
        return adr;
    }

    public void setAdr(double adr) {
        this.adr = adr;
    }

    public double getKd() {
        return kd;
    }

    public void setKd(double kd) {
        this.kd = kd;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
}
