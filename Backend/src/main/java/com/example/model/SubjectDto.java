package com.example.model; 
public class SubjectDto {

    private Long subjectId;
    private String title;

    // Constructor vacío (necesario para la deserialización)
    public SubjectDto() {}

    // Constructor con parámetros (opcional, pero útil)
    public SubjectDto(Long subjectId, String title) {
        this.subjectId = subjectId;
        this.title = title;
    }

    // Getters y setters (¡IMPRESCINDIBLES!)
    public Long getSubjectId() {
        return subjectId;
    }

    public void setSubjectId(Long subjectId) {
        this.subjectId = subjectId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }
}
