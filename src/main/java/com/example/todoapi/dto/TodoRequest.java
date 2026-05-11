package com.example.todoapi.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class TodoRequest {

    @NotBlank(message = "Title is required") //@Valid in controller checks req to these annotations
    @Size(max = 100, message = "Title must be at most 100 characters")
    private String title;

    @Size(max = 500, message = "Description must be at most 500 characters")
    private String description;

    private boolean completed;

    public TodoRequest() { //Uses no arg constructor for req (can use all arg with @JsonProperty, but this is default)
        //How it works: Req comes in as JSON, empty object constructed --> Look at JSON key value pair to match value to setter, use setter to set value
    }

    public TodoRequest(String title, String description, boolean completed) { //For dev testing
        this.title = title;
        this.description = description;
        this.completed = completed;
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
}
