package com.example.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.model.Subject;
import com.example.model.Subject_Mark;
import com.example.model.User;
import java.util.List;

public interface Subject_MarkRepository extends JpaRepository<Subject_Mark, Long> {

    boolean existsByStudentIdAndSubjectIdAndNameMark(User studentId, Subject subjectId, String nameMark);

    Optional<Subject_Mark> findByStudentIdAndSubjectIdAndNameMark(User studentId, Subject subjectId, String nameMark);

    List<Subject_Mark> findByStudentId(User studentId);

    Subject_Mark findFirstByOrderBySubjectMarkIdDesc();
}
