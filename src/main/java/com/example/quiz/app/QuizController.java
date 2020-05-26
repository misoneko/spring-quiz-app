package com.example.quiz.app;

import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.example.quiz.entity.Quiz;
import com.example.quiz.service.QuizService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Controller
@RequestMapping("/quiz")
public class QuizController {
    
    private final QuizService quizService;

    @Autowired
    public QuizController(QuizService quizService){
        this.quizService = quizService;
    }

    @GetMapping
    public String index(Model model){
        
        List<Quiz> list = quizService.findAll();

        Random rand = new Random();
        int listSize = list.size();
        Quiz selectedQuiz = list.get(rand.nextInt(listSize));
        model.addAttribute("selectedQuiz", selectedQuiz);
        model.addAttribute("title", "ランダムに出題");

        return "quiz/index";
    }

    @PostMapping("/answer")
    public String answer(
        @RequestParam("questionId") int questionId,
        @RequestParam("selectedChoice") String selectedChoice,
        Model model){
        
        Optional<Quiz> quizOpt = quizService.getQuiz(questionId);
        Quiz selectedQuiz = quizOpt.get();

        System.out.println(questionId);
        System.out.println(selectedChoice);

        if (selectedQuiz.getAnswer().equals(selectedChoice)){
            System.out.println("正解の方");
            model.addAttribute("isCorrect", true);
        } else {
            System.out.println("不正解の方");
            model.addAttribute("isCorrect", false);
        }
        
        model.addAttribute("title", "答え合わせ");
        model.addAttribute("selectedQuiz", selectedQuiz);

        return "quiz/answer";
    }
}