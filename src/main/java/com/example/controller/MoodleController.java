package com.example.controller;

import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.model.Subject;
import com.example.model.Subject_Mark;
import com.example.model.User;
import com.example.services.NotificationService;
import com.example.services.SubjectMarkService;
import com.example.services.SubjectService;
import com.example.services.UserService;

@RestController
@RequestMapping("/api/moodle")
public class MoodleController {


    @Autowired
    private UserService userService; 

    @Autowired
    private SubjectMarkService subjectMarkService; 

    @Autowired
    private SubjectService subjectService;

    @Autowired
    private MyWebSocketController webSocketController; // Inyecta MyWebSocketController

    @Autowired
    private NotificationService notificationService;

    @PostMapping("/updateGrade")
    public ResponseEntity<String> miEndpoint(@RequestBody Map<String, Object> datos) throws Exception {

        try {
            // Obtener los datos de la solicitud
            Long userId = Long.parseLong(datos.get("userid").toString());
            if (userId != -1) {
                Long courseId = Long.parseLong(datos.get("courseid").toString());
                int mark = Integer.parseInt(datos.get("grade").toString());
                String assignmentName = datos.get("assignmentname").toString();

                // Buscar el usuario y la asignatura
                User student = userService.findById(userId);
                Subject subject = subjectService.getSubject(courseId);

                // Crear una nueva nota o actualizarla si existe
                if (!subjectMarkService.existsByStudentIdAndSubjectIdAndNameMark(student, subject, assignmentName)) {
                    Subject_Mark newMark = new Subject_Mark(student, subject, mark, "Ordinaria", assignmentName); 
                    subjectMarkService.save(newMark);
                } else {
                    updateExistingMark(student, subject, mark, assignmentName);
                }

                webSocketController.sendUpdate(); // Llama al método sendUpdate
                notificationService.newNote(userId, subject.getTitle(), assignmentName, String.valueOf(mark), "Ordinaria");
                return ResponseEntity.ok("Nota creada/actualizada correctamente");
            }
            return ResponseEntity.ok("ok");
        } catch (NumberFormatException e) {
            return ResponseEntity.badRequest().body("Error al convertir los datos");
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    
    
}

    private void updateExistingMark(User student, Subject subject, int mark, String assignmentName) {
        Subject_Mark existingMark = subjectMarkService.findByStudentIdAndSubjectIdAndNameMark(student, subject, assignmentName).get();
        existingMark.setMark(mark);
        subjectMarkService.save(existingMark);
    }
}