package com.example;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.model.Convocatoria;
import com.example.model.Subject;
import com.example.model.User;
import com.example.repository.SubjectRepository;
import com.example.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class DataLoader {
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private SubjectRepository subjectRepository;

    @Autowired
	private PasswordEncoder passwordEncoder;

    @PostConstruct
    public void init() throws IOException, URISyntaxException {
        // Crear asignaturas de ejemplo
        Subject subject1 = new Subject(10L, "Historia");
        Subject subject2 = new Subject(20L, "Matemáticas");
        Subject subject3 = new Subject(30L, "Historia de la filología moderna");

        Convocatoria conv1 = new Convocatoria("10/11/2024", 1, "Aulario I, 2002");
        Convocatoria conv2 = new Convocatoria("10/9/2024", 2, "Aulario II, 2002");

        subject1.getConvocatories().add(conv1);
        subject1.getConvocatories().add(conv2);

        // Guardar asignaturas primero
        subjectRepository.saveAll(List.of(subject1, subject2, subject3));

        // Crear usuarios de ejemplo
        User user1 = new User(1L, "John", "Doe", "Smith", "12345678A","mariscalalonso16@icloud.com",passwordEncoder.encode("123"));
        User user2 = new User(2L, "Jane", "Doe", "Smith", "87654321B","yaovi@icloud.com",passwordEncoder.encode("123"));

        // Interconexión después de guardar las asignaturas
        user1.getSubjects().addAll(List.of(subject1, subject2));
        user2.getSubjects().add(subject3);

        //ROLES ASSIGN
        user1.setRoles(List.of("USER"));

        user2.setRoles(List.of("USER"));

        // Guardar usuarios
        userRepository.saveAll(List.of(user1, user2));
    }
}
