package com.example.todoapi.repository;

import com.example.todoapi.model.Todo;
import com.example.todoapi.model.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository //By extending JpaRepository you also tell spring to create an implementation of the interface
public interface TodoRepository extends JpaRepository<Todo, Long> { //Interface because spring generates implementation (class) itself at start, only define what repo handles and the type of PK

    Page<Todo> findByUser(User user, Pageable pageable);

    Page<Todo> findByUserAndCompleted(User user, boolean completed, Pageable pageable); //Method name parsing: findBy + column name + And + column name (SQL: SELECT * FROM todos WHERE User = ? AND completed = ?)
}
//SELECT * FROM todos WHERE user_id(Column from name parsing) = ?(value from parameter value)

//Why interface?
//2 reasons:
//1. JpaRepository contains methods that we need, but just the blueprints
//2. Our custom methods rely on spring handling them as we use, for example, method name parsing which isn't actual code, spring reads it and understands what code to write to match

//So how does it work?
//JpaRepository is an interface, meaning that it doesn't contain any implementation code
//If it wasn't an interface, spring wouldn't generate true code from none of our custom methods or JpaRepository methods
//Because the repos are interfaces, spring can generate the implementation for us --> It looks at Jpa methods and our custom methods as well as the name parsing for them --> Creates the actual code for all of them in class implementation

//Starting the application wouldn't work as name parsing is pure nonsense without spring, and Jpa methods wouldn't exist
//**We define what spring should do through the interface, using things like name parsing which tells spring specifically what to generate out of the interface**

//Repositories can be written as classes, meaning that developer writes all the code for the methods directly inside, both custom and basic crud
//The reason we use interface and spring to do it is because it's simply more convenient and foolproof