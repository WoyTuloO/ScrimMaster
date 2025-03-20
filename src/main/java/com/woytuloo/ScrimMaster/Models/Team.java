package com.woytuloo.ScrimMaster.Models;

import jakarta.persistence.*;

@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id" ,unique = true, nullable = false)
    private Long teamId;

    @Column(unique = true, nullable = false)
    private String teamName;
    private Long captainId;
    private Long player2Id;
    private Long player3Id;
    private Long player4Id;
    private Long player5Id;
    private Long player6Id;
    private Long player7Id;

    private Integer teamRanking = 0;

    public Team() {}

    public Team(String teamName, Long captainId) {
        this.captainId = captainId;
        this.teamName = teamName;
    }

    public Long getId() {
        return teamId;
    }

    public Long getTeamId() {
        return teamId;
    }

    public Long getCaptainId() {
        return captainId;
    }

    public void setCaptainId(Long captainId) {
        this.captainId = captainId;
    }

    public Long getPlayer2Id() {
        return player2Id;
    }

    public void setPlayer2Id(Long player2Id) {
        this.player2Id = player2Id;
    }

    public Long getPlayer3Id() {
        return player3Id;
    }

    public void setPlayer3Id(Long player3Id) {
        this.player3Id = player3Id;
    }

    public Long getPlayer4Id() {
        return player4Id;
    }

    public void setPlayer4Id(Long player4Id) {
        this.player4Id = player4Id;
    }

    public Long getPlayer5Id() {
        return player5Id;
    }

    public void setPlayer5Id(Long player5Id) {
        this.player5Id = player5Id;
    }

    public Long getPlayer6Id() {
        return player6Id;
    }

    public void setPlayer6Id(Long player6Id) {
        this.player6Id = player6Id;
    }

    public Long getPlayer7Id() {
        return player7Id;
    }

    public void setPlayer7Id(Long player7Id) {
        this.player7Id = player7Id;
    }

    public Integer getTeamRanking() {
        return teamRanking;
    }

    public void setTeamRanking(Integer teamRanking) {
        this.teamRanking = teamRanking;
    }

    public String getTeamname() {
        return teamName;
    }

    public void setTeamname(String username) {
        this.teamName = username;
    }
}
