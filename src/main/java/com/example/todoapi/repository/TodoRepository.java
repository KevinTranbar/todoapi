package com.example.todoapi.repository;

import com.example.todoapi.model.Todo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TodoRepository extends JpaRepository<Todo, Long> { //Interface because spring generates implementation (class) itself at start, only define what repo handles and the type of PK
    //We will come back here for more, don't forget
}
