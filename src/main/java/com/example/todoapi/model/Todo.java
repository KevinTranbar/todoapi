package com.example.todoapi.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "todos")
public class Todo {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY) //Auto generates id
    private Long  id;

    @Column(length = 100, nullable = false)
    private String title;

    @Column(length = 500)
    private String description;

    @Column(nullable = false)
    private boolean completed;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt;

    @PrePersist //Creates time before saved to database
    protected void onCreate() {
        this.createdAt = LocalDateTime.now();
    }

    public Todo() {

    }

    public Todo(Long id, String title, String description, boolean completed, LocalDateTime createdAt) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.completed = completed;
        this.createdAt = createdAt;
    }

    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }
    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }

    public boolean isCompleted() {
        return completed;
    }
    public void setCompleted(boolean completed) {
        this.completed = completed;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
