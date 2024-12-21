package com.example.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.User;
import com.example.model.Vote;
import com.example.model.Events.Event;
import com.example.model.Events.VoteDelegateEvent;
import com.example.repository.EventRepository;
import com.example.repository.VotesRepository;

@Service
public class VotesService {

    @Autowired
    private VotesRepository votesRepository;

    @Autowired
    private EventRepository eventRepository;

    @Autowired
    private UserService userService;

    public List<Vote> getAllVotesByEvent(Event event) {
        return votesRepository.findAllByEvent(event);

    }

    public boolean hasUserAlreadyVoted(User voter, Long event) {
        return votesRepository.existsByVoterAndEvent(voter, eventRepository.getReferenceById(event));
    }

    public Long createVote(User voter, Long voted, Long event) {
        if (hasUserAlreadyVoted(voter, event)) {
            throw new IllegalStateException("El usuario ya ha votado en este evento.");
        }
        Vote vote = new Vote();
        vote.setVoter(voter);
        vote.setVoted(userService.findById(voted));
        vote.setEvent((VoteDelegateEvent) eventRepository.getReferenceById(event));
        votesRepository.save(vote);
        return vote.getId();
    }

    public List<Object[]> getAllVotesByIdAndEvent(Long eventId) {
        return votesRepository.countVotesByVotedAndEvenet(eventRepository.findById(eventId).get());
    }

}
