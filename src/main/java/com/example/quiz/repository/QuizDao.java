package com.example.quiz.repository;

import java.util.List;
import java.util.Optional;

import com.example.quiz.entity.Quiz;

public interface QuizDao {
    
    List<Quiz> findAll();

    Optional<Quiz> findById(int id);

    void insert(Quiz quiz);

    int update(Quiz quiz);

    int deleteById(int id);

}