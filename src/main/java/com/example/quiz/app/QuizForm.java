package com.example.quiz.app;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.springframework.web.multipart.MultipartFile;

public class QuizForm {
    
    @NotNull(message = "問題文を入力してください")
    @Size(min = 1, max = 200, message = "200文字以内で入力してください")
    private String question;

    @NotNull(message = "選択肢1を入力してください")
    @Size(min = 1, max = 200, message = "200文字以内で入力してください")
    private String choice1;

    @NotNull(message = "選択肢2を入力してください")
    @Size(min = 1, max = 200, message = "200文字以内で入力してください")
    private String choice2;

    @NotNull(message = "選択肢3を入力してください")
    @Size(min = 1, max = 200, message = "200文字以内で入力してください")
    private String choice3;

    @NotNull(message = "選択肢4を入力してください")
    @Size(min = 1, max = 200, message = "200文字以内で入力してください")
    private String choice4;

    @NotNull(message = "答えを入力してください")
    private String answer;

    private MultipartFile uploadFile;
    private String fileName;

    private int id;

    public QuizForm(){}

    public String getQuestion() {
        return question;
    }

    public void setQuestion(String question) {
        this.question = question;
    }

    public String getChoice1() {
        return choice1;
    }

    public void setChoice1(String choice1) {
        this.choice1 = choice1;
    }

    public String getChoice2() {
        return choice2;
    }

    public void setChoice2(String choice2) {
        this.choice2 = choice2;
    }

    public String getChoice3() {
        return choice3;
    }

    public void setChoice3(String choice3) {
        this.choice3 = choice3;
    }

    public String getChoice4() {
        return choice4;
    }

    public void setChoice4(String choice4) {
        this.choice4 = choice4;
    }

    public String getAnswer() {
        return answer;
    }

    public void setAnswer(String answer) {
        this.answer = answer;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public MultipartFile getUploadFile() {
        return uploadFile;
    }

    public void setUploadFile(MultipartFile uploadFile) {
        this.uploadFile = uploadFile;
    }

    public String getFileName() {
        return fileName;
    }

    public void setFileName(String fileName) {
        this.fileName = fileName;
    }

    

    

}