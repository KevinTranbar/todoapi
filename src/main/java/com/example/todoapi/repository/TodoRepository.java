package com.example.todoapi.repository;

import com.example.todoapi.model.Todo;
import com.example.todoapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> { //Interface because spring generates implementation (class) itself at start, only define what repo handles and the type of PK

    Page<Todo> findByUser(User user, Pageable pageable);

    Page<Todo> findByUserAndCompleted(User user, boolean completed, Pageable pageable); //Method name parsing: findBy + column name + And + column name (SQL: SELECT * FROM todos WHERE User = ? AND completed = ?)
}
//SELECT * FROM todos WHERE user_id(Column from name parsing) = ?(value from parameter value)
