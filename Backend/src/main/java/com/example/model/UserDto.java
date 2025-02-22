package com.example.model;

public class UserDto{ 
    private Long studentId;
    private String name;
    private String surname1;
    private String dni;
  
    public UserDto(){}
  
    public Long getStudentId() {
      return studentId;
     }
  
     public void setStudentId(Long id) {
      this.studentId = id;
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

    public String getDni() {
      return dni;
    }

    public void setDni(String dni) {
      this.dni = dni;
    }

     
  }
