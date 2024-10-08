package com.example.model;

import java.sql.Blob;
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

@Entity
public class User {

    @Id
    private Long studentId;
    
    private String name;
    private String surname1;
    private String surname2;
    private String dni;
    @ElementCollection(fetch = FetchType.EAGER)
	private List<String> roles;
    private Blob photo;
    private boolean isCandidate;
    private int votes;
    private String email;
	private String password;

    @ManyToMany
    @JoinTable(
        name = "user_subject",
        joinColumns = @JoinColumn(name = "userId"),
        inverseJoinColumns = @JoinColumn(name = "subjectId"))
    private Collection<Subject> subjects = new ArrayList<Subject>();

    public User() {}
    public User(Long studentId, String name, String surname1, String surname2, String dni, String email, String password) {
        this.studentId = studentId;
        this.name= name;
        this.surname1=surname1;
        this.surname2=surname2;
        this.dni=dni;
        this.photo=null;
        votes = 0;
        isCandidate = false;
        this.email = email;
        this.password = password;
    }

    public User(Long studentId, String name, String surname1, String surname2, String dni, Blob photo, String email) {
        this(studentId, name, surname1, surname2, dni,email,"123456");
        this.photo=photo;
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

    public int getVotes() {
        return votes;
    }

    public void setVotes(int votes) {
        this.votes = votes;
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
    

    

    

    

    
    
}
