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
    private String category;


    public News(String title, String description, String category) {
        this.title = title;
        this.description = description;
        this.category = category;
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

    


}

