package com.example.quiz.service;

import java.util.List;
import java.util.Optional;

import com.example.quiz.entity.Quiz;
import com.example.quiz.repository.QuizDao;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.stereotype.Service;

@Service
public class QuizServiceImpl implements QuizService {

    private final QuizDao dao;

    @Autowired
    public QuizServiceImpl(QuizDao dao) {
        this.dao = dao;
    }

    
    @Override
    public List<Quiz> findAll() {
        return dao.findAll();
    }

    @Override
    public Optional<Quiz> getQuiz(int id) {
        try{
            return dao.findById(id);
        }catch(EmptyResultDataAccessException e){
            throw new QuizNotFoundException("対象のクイズが存在しません");
        }
    }

    @Override
    public void insert(Quiz quiz) {
        dao.insert(quiz);
    }
    
    @Override
    public void update(Quiz quiz) {
        if(dao.update(quiz) == 0){
            throw new QuizNotFoundException("更新対象のクイズが存在しません");
        }
    }
    
    @Override
    public void deleteById(int id) {
        if(dao.deleteById(id) == 0){
            throw new QuizNotFoundException("削除対象のクイズが存在しません");
        }
    }
    

}