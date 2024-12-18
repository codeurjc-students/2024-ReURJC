package com.example.model;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.sql.Blob;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;

import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import java.util.List;

import javax.sql.rowset.serial.SerialBlob;

import com.fasterxml.jackson.annotation.JsonIgnore;

@Entity
public class User {

    @Id
    @JsonIgnore
    private Long studentId;

    private String name;
    private String surname1;
    private String surname2;
    private String dni;
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> roles;
    @JsonIgnore
    private Blob photo;
    private boolean isCandidate;
    @JsonIgnore
    private String email;
    @JsonIgnore
    private String password;
    @JsonIgnore
    @ElementCollection(fetch = FetchType.EAGER)
    private List<String> fcmToken = new ArrayList<String>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "user_subject", joinColumns = @JoinColumn(name = "userId"), inverseJoinColumns = @JoinColumn(name = "subjectId"))
    private Collection<Subject> subjects = new ArrayList<Subject>();

    public User() {
    }

    public User(Long studentId, String name, String surname1, String surname2, String dni, String email,
            String password) {
        this.studentId = studentId;
        this.name = name;
        this.surname1 = surname1;
        this.surname2 = surname2;
        this.dni = dni;
        isCandidate = false;
        this.email = email;
        this.password = password;
        try {
            // Usar ClassLoader para cargar el archivo desde el classpath
            InputStream imageStream = getClass().getClassLoader().getResourceAsStream("image.png");
            if (imageStream != null) {
                // Leer los bytes de la imagen
                byte[] imageBytes = imageStream.readAllBytes();
                imageStream.close();

                // Crear un Blob a partir de los bytes
                Blob imageBlob = new SerialBlob(imageBytes);

                photo = imageBlob;
            } else {
                System.err.println("No se encontró el archivo image.png en el classpath.");
            }
        } catch (IOException | SQLException e) {
            e.printStackTrace();
        }
    }

    public User(Long studentId, String name, String surname1, String surname2, String dni, Blob photo, String email) {
        this(studentId, name, surname1, surname2, dni, email, "123456");
        this.photo = photo;
    }

    public Long getStudentId() {
        return studentId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname1() {
        return surname1;
    }

    public void setSurname1(String surname1) {
        this.surname1 = surname1;
    }

    public String getSurname2() {
        return surname2;
    }

    public void setSurname2(String surname2) {
        this.surname2 = surname2;
    }

    public String getDni() {
        return dni;
    }

    public void setDni(String dni) {
        this.dni = dni;
    }

    public Blob getPhoto() {
        return photo;
    }

    public void setPhoto(Blob photo) {
        this.photo = photo;
    }

    public boolean isCandidate() {
        return isCandidate;
    }

    public void setCandidate(boolean isCandidate) {
        this.isCandidate = isCandidate;
    }

    public Long getId() {
        return this.studentId;
    }

    public Collection<Subject> getSubjects() {
        return subjects;
    }

    public String getEmail() {
        return email;
    }

    public List<String> getRoles() {
        return roles;
    }

    public void setRoles(List<String> roles) {
        this.roles = roles;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public List<String> getFcmToken() {
        return fcmToken;
    }

    public void addFcmToken(String fcmToken) {
        this.fcmToken.add(fcmToken);
    }

}
