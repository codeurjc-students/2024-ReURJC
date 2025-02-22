package com.example.model;



import java.time.LocalDateTime;
import java.util.List;

public class AttendanceDto {
    private Long id;
    private LocalDateTime dateTime;
    private Long creator;
    private SubjectDto subject;
    private String code;
    private List<UserAttendanceDto> usersPresent;

    // Constructor vacío (NECESARIO para la deserialización)
    public AttendanceDto() {}

    // Getters y setters (NECESARIOS)

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public LocalDateTime getDateTime() {
        return dateTime;
    }

    public void setDateTime(LocalDateTime dateTime) {
        this.dateTime = dateTime;
    }

    public Long getCreator() {
        return creator;
    }

    public void setCreator(Long creator) {
        this.creator = creator;
    }

    public SubjectDto getSubject() {
        return subject;
    }

    public void setSubject(SubjectDto subject) {
        this.subject = subject;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public List<UserAttendanceDto> getUsersPresent() {
        return usersPresent;
    }

    public void setUsersPresent(List<UserAttendanceDto> usersPresent) {
        this.usersPresent = usersPresent;
    }
}

