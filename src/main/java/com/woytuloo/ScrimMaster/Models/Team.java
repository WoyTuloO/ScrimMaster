package com.woytuloo.ScrimMaster.Models;

import jakarta.persistence.*;

@Entity
public class Team {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "id", unique = true, nullable = false)
    private Long teamId;

    @Column(unique = true, nullable = false)
    private String teamName;

    // Relacja do kapitana – użytkownik, który jest właścicielem drużyny
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "captain_id", nullable = false)
    private User captain;

    // Pozostali gracze – opcjonalnie, mogą być null, jeśli jeszcze nie dołączyli do drużyny
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player2_id")
    private User player2;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player3_id")
    private User player3;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player4_id")
    private User player4;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player5_id")
    private User player5;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player6_id")
    private User player6;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "player7_id")
    private User player7;

    private Integer teamRanking = 0;

    public Team() {}

    public Team(String teamName, User captain) {
        this.teamName = teamName;
        this.captain = captain;
    }

    public Long getTeamId() {
        return teamId;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }

    public User getCaptain() {
        return captain;
    }

    public void setCaptain(User captain) {
        this.captain = captain;
    }

    public User getPlayer2() {
        return player2;
    }

    public void setPlayer2(User player2) {
        this.player2 = player2;
    }

    public User getPlayer3() {
        return player3;
    }

    public void setPlayer3(User player3) {
        this.player3 = player3;
    }

    public User getPlayer4() {
        return player4;
    }

    public void setPlayer4(User player4) {
        this.player4 = player4;
    }

    public User getPlayer5() {
        return player5;
    }

    public void setPlayer5(User player5) {
        this.player5 = player5;
    }

    public User getPlayer6() {
        return player6;
    }

    public void setPlayer6(User player6) {
        this.player6 = player6;
    }

    public User getPlayer7() {
        return player7;
    }

    public void setPlayer7(User player7) {
        this.player7 = player7;
    }

    public Integer getTeamRanking() {
        return teamRanking;
    }

    public void setTeamRanking(Integer teamRanking) {
        this.teamRanking = teamRanking;
    }
}
