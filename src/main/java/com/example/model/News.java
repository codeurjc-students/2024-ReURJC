package com.example.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

@Entity
public class News {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long newsId;
    private String title;
    private String description;
    private String littleDescription;
    private String category;


    public News(String title, String description, String category, String littleDescription) {
        this.title = title;
        this.description = description;
        this.category = category;
        this.littleDescription = littleDescription;
    }


    public Long getNewsId() {
        return newsId;
    }


    public String getTitle() {
        return title;
    }


    public String getDescription() {
        return description;
    }


    public String getCategory() {
        return category;
    }


    public String getLittleDescription() {
        return littleDescription;
    }

    

    


}

