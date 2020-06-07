package com.example.quiz.repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import com.example.quiz.entity.Quiz;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

@Repository
public class QuizDaoImpl implements QuizDao {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public QuizDaoImpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    @Override
    public List<Quiz> findAll() {
        
        String sql = "SELECT id, question, choice1, choice2, choice3, choice4, answer, file_name "
                        + "FROM quiz";
        
        List<Map<String, Object>> resultList = jdbcTemplate.queryForList(sql);
        
        List<Quiz> quizList = new ArrayList<Quiz>();
        for(Map<String, Object> result : resultList){
            Quiz quiz = new Quiz();
            quiz.setId((int)result.get("id"));
            quiz.setQuestion((String)result.get("question"));
            quiz.setChoice1((String)result.get("choice1"));
            quiz.setChoice2((String)result.get("choice2"));
            quiz.setChoice3((String)result.get("choice3"));
            quiz.setChoice4((String)result.get("choice4"));
            quiz.setAnswer((String)result.get("answer"));
            quiz.setFileName((String)result.get("file_name"));

            quizList.add(quiz);
        }

        return quizList;
    }

    
    
    @Override
    public Optional<Quiz> findById(int id) {
        
        String sql = "SELECT id, question, choice1, choice2, choice3, choice4, answer, file_name "
                        + "FROM quiz WHERE id = ?";

        Map<String, Object> result = jdbcTemplate.queryForMap(sql, id);

        Quiz quiz = new Quiz();
        quiz.setId((int)result.get("id"));
        quiz.setQuestion((String)result.get("question"));
        quiz.setChoice1((String)result.get("choice1"));
        quiz.setChoice2((String)result.get("choice2"));
        quiz.setChoice3((String)result.get("choice3"));
        quiz.setChoice4((String)result.get("choice4"));
        quiz.setAnswer((String)result.get("answer"));
        quiz.setFileName((String)result.get("file_name"));

        Optional<Quiz> quizOpt = Optional.ofNullable(quiz);

        return quizOpt;
    }
    
    @Override
    public void insert(Quiz quiz) {
        jdbcTemplate.update(
            "INSERT INTO quiz(question, choice1, choice2, choice3, choice4, answer, file_name) VALUES (?, ?, ?, ?, ?, ?, ?)",
            quiz.getQuestion(), quiz.getChoice1(), quiz.getChoice2(), quiz.getChoice3(), quiz.getChoice4(), quiz.getAnswer(), quiz.getFileName()
        );
    }

    @Override
    public int update(Quiz quiz) {
        return jdbcTemplate.update(
            "UPDATE quiz SET question = ?, choice1 = ?, choice2 = ?, choice3 = ?, choice4 = ?, answer = ?, file_name = ? WHERE id = ?",
            quiz.getQuestion(), quiz.getChoice1(), quiz.getChoice2(), quiz.getChoice3(), quiz.getChoice4(), quiz.getAnswer(), quiz.getFileName(), quiz.getId()
        );
    }
    
    @Override
    public int deleteById(int id) {
        return jdbcTemplate.update(
            "DELETE FROM quiz WHERE id = ?",
            id
        );
    }
    
    
}