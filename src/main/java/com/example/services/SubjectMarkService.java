package com.example.services;

import com.example.model.Subject;
import com.example.model.Subject_Mark;
import com.example.model.User;
import com.example.repository.Subject_MarkRepository;

import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class SubjectMarkService {

    @Autowired
    private Subject_MarkRepository subjectMarkRepository;

    public Subject_Mark saveSubjectMark(Subject_Mark subjectMark) {
        return subjectMarkRepository.save(subjectMark);
    }

    public void save (Subject_Mark obj) {
        subjectMarkRepository.save(obj);
    }

    public boolean existsByStudentIdAndSubjectIdAndNameMark(User student, Subject subject, String nameMark){
        return subjectMarkRepository.existsByStudentIdAndSubjectIdAndNameMark(student, subject, nameMark);
    }

    public Optional<Subject_Mark> findByStudentIdAndSubjectIdAndNameMark(User student, Subject subject, String nameMark) {
        return subjectMarkRepository.findByStudentIdAndSubjectIdAndNameMark(student, subject, nameMark);
    }

    public List<Subject_Mark> findSubjectsByStudent(User student) {
        return subjectMarkRepository.findByStudentId(student);
    }

    public Subject_Mark getLastSubjectMarkAdded() {
        return subjectMarkRepository.findFirstByOrderBySubjectMarkIdDesc();
    }
}