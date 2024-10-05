package com.example.model;

public class ConvocatoryInfo {
    private String classroom;
    private String date;
    private String convocatory;

    public ConvocatoryInfo(String classroom, String date, String convocatory) {
        this.classroom = classroom;
        this.date = date;
        this.convocatory = convocatory;

    }

    public String getClassroom() {
        return classroom;
    }

    public String getDate() {
        return date;
    }

    public String getConvocatory() {
        return convocatory;
    }

    

}
