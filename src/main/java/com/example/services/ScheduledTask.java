package com.example.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
public class ScheduledTask {
    @Autowired
    private  EventService eventService;

    @Scheduled(cron = "0 0 0 * * ?")
    public void scheduleVotingEnd() {
        
        
                if ( !eventService.isVoteDelegatesEvent())
        eventService.endVotingEvent();
    }
}
    
