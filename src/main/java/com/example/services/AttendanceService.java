package com.example.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.model.Attendance;
import com.example.model.Subject;
import com.example.model.User;
import com.example.model.UserAttendance;
import com.example.repository.AttendanceRepository;
import com.example.repository.UserAttendanceRepository;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserAttendanceRepository userAttendanceRepository;

    public Attendance newAttendance(User user, Subject subject){
        Attendance attendance = new Attendance(user,subject);
        attendanceRepository.save(attendance);
        return attendance;

    }

    public void adduser(Attendance attendance, User user) {
        UserAttendance userregistry = new UserAttendance(user, attendance);
        userAttendanceRepository.save(userregistry);
        attendance.addUser(userregistry);
        attendanceRepository.save(attendance);
    }

    public List<Attendance> getAllAttendances(User user){
        return attendanceRepository.findTop10ByCreatorOrderByIdDesc(user);
    }

    public Attendance getAttendanceEvent(String code) {
        return attendanceRepository.findByCode(code);
    }
    
}
