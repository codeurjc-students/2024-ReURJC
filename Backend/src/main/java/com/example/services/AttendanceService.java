package com.example.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

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

    private static final int CODE_LENGTH = 6;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random random = new SecureRandom();

    public Attendance newAttendance(User user, Subject subject) {
        Attendance attendance = new Attendance(user, subject, generateRandomCode(CODE_LENGTH));
        attendanceRepository.save(attendance);
        return attendance;

    }

    public void adduser(Attendance attendance, User user) {
        UserAttendance userregistry = new UserAttendance(user, attendance);
        userAttendanceRepository.save(userregistry);
        attendance.addUser(userregistry);
        attendanceRepository.save(attendance);
    }

    public List<Attendance> getAllAttendances(User user) {
        return attendanceRepository.findTop10ByCreatorOrderByIdDesc(user);
    }

    public Attendance getAttendanceEvent(String code) {
        return attendanceRepository.findByCode(code);
    }

    public boolean isCodeUsed(String code) {
        return this.attendanceRepository.existsByCode(code);
    }

    public boolean addTime(Long id) {
        Attendance attendance = this.attendanceRepository.findById(id).get();
        attendance.setDateTime(LocalDateTime.now().plusHours(1));
        attendanceRepository.save(attendance);

        return true;

    }

    public Attendance getAttendanceById(Long id) {
        return attendanceRepository.findById(id).get();
    }

    private String generateRandomCode(int length) {
        String code;
        do {
            StringBuilder codeBuilder = new StringBuilder(length);
            for (int i = 0; i < length; i++) {
                codeBuilder.append(CHARACTERS.charAt(random.nextInt(CHARACTERS.length())));
            }
            code = codeBuilder.toString();
        } while (attendanceRepository.existsByCode(code));
        return code;
    }
    

}
