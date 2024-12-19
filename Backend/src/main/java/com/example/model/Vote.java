package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.JoinColumn;
import java.time.LocalDateTime;

import com.example.model.Events.VoteDelegateEvent;

@Entity
public class Vote {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "voter_id", nullable = false)
    private User voter; // El usuario que vota

    @ManyToOne
    @JoinColumn(name = "voted_id", nullable = false)
    private User voted; // El usuario que recibe el voto

    @ManyToOne
    @JoinColumn(name = "event_id", nullable = false)
    private VoteDelegateEvent event; // El evento asociado al voto

    private LocalDateTime voteDate;

    public Vote() {
        this.voteDate = LocalDateTime.now();
    }

    public Vote(User voter, User voted, VoteDelegateEvent event) {
        this();
        this.voter = voter;
        this.voted = voted;
        this.event = event;
    }

    // Getters y setters

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public User getVoter() {
        return voter;
    }

    public void setVoter(User voter) {
        this.voter = voter;
    }

    public User getVoted() {
        return voted;
    }

    public void setVoted(User voted) {
        this.voted = voted;
    }

    public VoteDelegateEvent getEvent() {
        return event;
    }

    public void setEvent(VoteDelegateEvent event) {
        this.event = event;
    }

    public LocalDateTime getVoteDate() {
        return voteDate;
    }

    public void setVoteDate(LocalDateTime voteDate) {
        this.voteDate = voteDate;
    }
}
