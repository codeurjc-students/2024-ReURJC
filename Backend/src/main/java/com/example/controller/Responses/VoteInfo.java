package com.example.controller.Responses;

public class VoteInfo {

    private Long votedId; // El usuario que recibe el voto
    private Long event; // El evento asociado al voto


    public VoteInfo(Long votedId, Long event) {
        this.votedId = votedId;
        this.event = event;
    }


    public Long getVotedId() {
        return votedId;
    }

    public Long getEvent() {
        return event;
    }
}