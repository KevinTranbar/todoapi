package com.example.todoapi.service;

import com.example.todoapi.dto.TodoRequest;
import com.example.todoapi.exception.TodoNotFoundException;
import com.example.todoapi.model.Todo;
import com.example.todoapi.dto.TodoResponse;
import com.example.todoapi.model.User;
import com.example.todoapi.repository.TodoRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class TodoService {

    private final TodoRepository todoRepository;
    private final UserService userService;

    public TodoService(TodoRepository todoRepository, UserService userService) {
        // Constructor injection: TodoService needs a TodoRepository and UserService to function.
        // At startup, Spring resolves dependencies bottom-up = creates TodoRepository
        // first, then any of ITS dependencies, and so on until reaching a constructor
        // with no bean dependencies. Basically = Create arg object first
        this.todoRepository = todoRepository;
        this.userService = userService;
    }

    private TodoResponse toResponse(Todo todo) {
        return new TodoResponse(
                todo.getId(),
                todo.getTitle(),
                todo.getDescription(),
                todo.isCompleted(),
                todo.getCreatedAt()
        );
    }

    public Page<TodoResponse> getAllTodos(Boolean completed, Pageable pageable) {
        User currentUser = userService.getCurrentUser();
        Page<Todo> todos;

        if (completed != null) {
            todos = todoRepository.findByUserAndCompleted(currentUser, completed, pageable);
        } else {
            todos = todoRepository.findByUser(currentUser, pageable);
        }

        return todos.map(todo -> toResponse(todo));
    }

    public TodoResponse getTodoById(Long id) {
        User currentUser = userService.getCurrentUser();

        Todo todo = todoRepository.findById(id) //Instead of custom repo method like findByUserAndId --> Below
                .orElseThrow(() -> new TodoNotFoundException(id));

        if (!todo.getUser().getId().equals(currentUser.getId())) { //Fetch the todo by id (could be anybodies todo), check if it belongs to the current user with id, if not, reject = Effectively only shows todos per user and id without custom method in repo
            throw new TodoNotFoundException(id);
        }

        return toResponse(todo);
    }

    public TodoResponse createTodo(TodoRequest request) {
        User currentUser = userService.getCurrentUser();

        Todo todo = new Todo();
        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.isCompleted());
        todo.setUser(currentUser); //Wants to save whole user to todo, but ManyToOne relationship stops it and only saves id because it knows other info already in User entity

        Todo saved = todoRepository.save(todo);
        return toResponse(saved);
    }

    public TodoResponse updateTodo(Long id, TodoRequest request) {
        User currentUser = userService.getCurrentUser();

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));

        if (!todo.getUser().getId().equals(currentUser.getId())) { //Further notes: Basically owner verification, can't modify another's todo
            throw new TodoNotFoundException(id); //Further, further notes: Can't use ...getUser().equals(currentUser) because user from todo and user from context are separate objects in memory --> Can't be compared directly
        }                                        //getId() however returns two separate Long objects both holding the same value, so can be compared directly

        todo.setTitle(request.getTitle());
        todo.setDescription(request.getDescription());
        todo.setCompleted(request.isCompleted());

        Todo updated = todoRepository.save(todo);
        return toResponse(updated);
    }

    public void deleteTodo(Long id) {
        User currentUser = userService.getCurrentUser();

        Todo todo = todoRepository.findById(id)
                .orElseThrow(() -> new TodoNotFoundException(id));

        if (!todo.getUser().getId().equals(currentUser.getId())) {
            throw new TodoNotFoundException(id);
        }

        todoRepository.delete(todo);
    }
}
