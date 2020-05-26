package com.example.quiz.service;

public class QuizNotFoundException extends RuntimeException{
    
    private static final long serialVersionUID = 1L;

    public QuizNotFoundException(String message){
        super(message);
    }
}