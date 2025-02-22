package com.example.Asistance.services;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Random;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.Asistance.Model.Attendance;
import com.example.Asistance.Model.Subject;
import com.example.Asistance.Model.User;
import com.example.Asistance.Model.UserAttendance;
import com.example.Asistance.repository.AttendanceRepository;
import com.example.Asistance.repository.SubjectRepository;
import com.example.Asistance.repository.UserAttendanceRepository;
import com.example.Asistance.repository.UserRepository;

@Service
public class AttendanceService {

    @Autowired
    private AttendanceRepository attendanceRepository;

    @Autowired
    private UserAttendanceRepository userAttendanceRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
    private UserRepository userRepository;

    private static final int CODE_LENGTH = 6;
    private static final String CHARACTERS = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
    private static final Random random = new SecureRandom();

    public Attendance newAttendance(Long user, Subject subject) {
        Subject finalSubject;
        if (!subjectRepository.existsById(subject.getSubjectId())) {
            finalSubject = new Subject(subject.getSubjectId(), subject.getTitle());
            subjectRepository.save(finalSubject);
        } else {
            finalSubject = subjectRepository.getReferenceById(subject.getSubjectId());
        }

        Attendance attendance = new Attendance(user, finalSubject, generateRandomCode(CODE_LENGTH));
        attendanceRepository.save(attendance);
        
        
        return attendance;

    }

    public void adduser(Attendance attendance, User user) {
        User finalUser;
        if (!userRepository.existsById(user.getStudentId())) {
            finalUser = new User(user.getStudentId(), user.getName(), user.getSurname1(), user.getDni());
            userRepository.save(finalUser);
        } else {
            finalUser = userRepository.findById(user.getStudentId()).get();
        }
        UserAttendance userregistry = new UserAttendance(finalUser, attendance);
        userAttendanceRepository.save(userregistry);
        attendance.addUser(userregistry);
        attendanceRepository.save(attendance);
    }

    public List<Attendance> getAllAttendances(Long user) {
        
        
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
