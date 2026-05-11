package com.example.todoapi.dto;

import java.time.LocalDateTime;

public class TodoResponse { //Same info as Todo entity, why not use entity? = Entity contains annotations (not for JSON serialization), entity for DB communication, res dto for api communication
    //Both entity and res dto can be sent back as response, but res dto is more specific and clean

    //CONTEXT = EVERYTHING SENT OUT BY CONTROLLER GETS SERIALIZED TO JSON, DTO JUST WRAPS IT INTO JSON OBJECT(key value pairs) BECAUSE OF GETTERS, JACKSON FINDS GETTERS TO READ AND SERIALIZE PLUS GETTER BECOMES KEY IN KEY VALUE PAIR (Kinda)

    private Long id;
    private String title;
    private String description;
    private boolean completed;
    private LocalDateTime createdAt;

    public TodoResponse() { //Technically not needed (Only kept for learning purposes)

    }

    public TodoResponse(Long id, String title, String description, boolean completed, LocalDateTime createdAt) { //Uses all arg constructor for res (can use no arg, but pointless and messy)
        //How it works: Extract entity from DB via repo in service --> Use entity getters to read data and pass them as args to full arg res constructor -->
        //Create res dto, serialize automatically and wrap in JSON object, key value pair(Gets JSON key from getter (strips get/is and lowercases first letter))
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
