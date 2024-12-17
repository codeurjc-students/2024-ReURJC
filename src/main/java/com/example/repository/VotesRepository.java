package com.example.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.example.model.User;
import com.example.model.Vote;
import com.example.model.Events.Event;

public interface VotesRepository extends JpaRepository<Vote, Long> {

    List<Vote> findAllByEvent(Event event);

    boolean existsByVoterAndEvent(User voter, Event event);

    @Query("SELECT v.voted.id, COUNT(v.id) FROM Vote v GROUP BY v.voted.id")
    List<Object[]> countVotesByVotedAndEvenet(Event event);

}
