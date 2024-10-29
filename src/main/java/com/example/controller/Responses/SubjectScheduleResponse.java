package com.example.controller.Responses;

import java.util.List;

import com.example.model.Schedule;

public class SubjectScheduleResponse {
    private String title;
    private List<Schedule> schedule;

    public SubjectScheduleResponse(String title, List<Schedule> schedule) {
        this.title = title;
        this.schedule = schedule;
    }

    public String getTitle() {
        return title;
    }

    public List<Schedule> getSchedule() {
        return schedule;
    }
}
