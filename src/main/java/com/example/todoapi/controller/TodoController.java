package com.example.todoapi.controller;

import com.example.todoapi.dto.TodoRequest;
import com.example.todoapi.dto.TodoResponse;
import com.example.todoapi.service.TodoService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/todos")
public class TodoController {

    private final TodoService todoService;

    public TodoController(TodoService todoService) {
        this.todoService = todoService;
    }

    @GetMapping
    public List<TodoResponse> getAll() {
        return todoService.getAllTodos();
    }

    @GetMapping("/{id}") //Placeholder for id
    public TodoResponse getOne(@PathVariable Long id) { //Take id convert to Long and pass as parameter
        return todoService.getTodoById(id);
    }

    @PostMapping
    public ResponseEntity<TodoResponse> create(@Valid @RequestBody TodoRequest request) { //Take req body(JSON) and convert it into TodoRequest object (DTO)
        TodoResponse created = todoService.createTodo(request); //Create Todo with converted req body
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/{id}")
    public TodoResponse update(@PathVariable Long id, @Valid @RequestBody TodoRequest request) {
        return todoService.updateTodo(id, request);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        todoService.deleteTodo(id);
        return ResponseEntity.noContent().build();
    }
}
