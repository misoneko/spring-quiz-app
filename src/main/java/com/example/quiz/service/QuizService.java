package com.example.quiz.service;

import java.util.List;
import java.util.Optional;

import com.example.quiz.entity.Quiz;

public interface QuizService {
    
    List<Quiz> findAll();

    Optional<Quiz> getQuiz(int id);

    void insert(Quiz quiz);

    void update(Quiz quiz);

    void deleteById(int id);

}