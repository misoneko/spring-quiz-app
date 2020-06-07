package com.example.quiz.app;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Base64;
import java.util.List;
import java.util.Optional;
import java.util.Random;

import com.example.quiz.entity.Quiz;
import com.example.quiz.service.QuizService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;
    private final String uploadDirPath = "./src/main/resources/uploads/";

    @Autowired
    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping
    public String index(Model model) {
        return "quiz/index";
    }

    @GetMapping("/question")
    public String question(Model model) {
        List<Quiz> list = quizService.findAll();

        Random rand = new Random();
        int listSize = list.size();
        Quiz selectedQuiz = list.get(rand.nextInt(listSize));
        model.addAttribute("selectedQuiz", selectedQuiz);

        model.addAttribute("title", "ランダムに出題");

        String fileName = selectedQuiz.getFileName();
        System.out.println("ファイル名：" + fileName);
        if(fileName == null){ // アップロードファイルが無いクイズの場合
            return "quiz/question";
        }
        String base64data = ConvertToBase64(uploadDirPath + fileName);
        model.addAttribute("base64data", base64data);

        return "quiz/question";
    }

    @GetMapping("/create")
    public String create(QuizForm quizForm, Model model) {
        // 引数のquizFormは自動で入るので、明示的にmodelにaddしなくてok（completeの方の@Validatedの話も関連）
        // model.addAttribute("selectedQuizForm", quizForm);

        return "quiz/edit";
    }

    @RequestMapping(path = "/list", method = { RequestMethod.GET, RequestMethod.POST })
    public String list(Model model) {
        List<Quiz> list = quizService.findAll();
        model.addAttribute("quizList", list);

        return "quiz/list";
    }

    @PostMapping("/complete")
    public String complete(@Validated QuizForm quizForm, BindingResult result, @RequestParam("quizId") int quizId,
            // @RequestParam("uploadFile") MultipartFile multipartFile,
            Model model, RedirectAttributes redirectAttributes) {

        if (result.hasErrors()) {
            System.out.println("/complete内でresult.hasErrors");
            // System.out.println(result);

            // `@Validated`のエラー内容などを保持したままhtmlへ送りたい場合は、明示的にmodelへaddせず、自動処理に任せる
            // （html側の名称もquizFormとする必要あり）
            // model.addAttribute("selectedQuizForm", quizForm);

            // TODO: 画像付きのクイズで編集時にバリデーションエラーが発生した場合、
            //       画像を再表示しつつ、「もう一度画像を選択してください」の表示をさせないようにする

            return "quiz/edit";
        }

        Quiz quiz = makeQuiz(quizForm, quizId);

        // System.out.println("カレントディレクトリ：" + System.getProperty("user.dir"));

        if (quizId == 0) { // 新規作成の場合
            quizService.insert(quiz);
        } else { // 既存のクイズの場合
            quizService.update(quiz);
        }

        redirectAttributes.addFlashAttribute("complete", "登録が完了しました");

        return "redirect:/quiz/list";

    }

    @PostMapping("/delete")
    public String delete(@RequestParam("quizId") int quizId, Model model, RedirectAttributes redirectAttributes) {

        quizService.deleteById(quizId);
        redirectAttributes.addFlashAttribute("complete", "削除が完了しました");

        return "redirect:/quiz/list";
    }

    @PostMapping("/answer")
    public String answer(@RequestParam("quizId") int quizId, @RequestParam("selectedChoice") String selectedChoice,
            Model model) {

        Optional<Quiz> quizOpt = quizService.getQuiz(quizId);
        Quiz selectedQuiz = quizOpt.get();

        // System.out.println(quizId);
        // System.out.println(selectedChoice);

        if (selectedQuiz.getAnswer().equals(selectedChoice)) {
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

    @PostMapping("/edit")
    public String edit(@RequestParam("quizId") int quizId, Model model) {
        System.out.println("quizId" + quizId);

        Optional<Quiz> quizOpt = quizService.getQuiz(quizId);
        Quiz selectedQuiz = quizOpt.get();
        QuizForm selectedQuizForm = makeQuizForm(selectedQuiz);

        model.addAttribute("quizForm", selectedQuizForm);

        String fileName = selectedQuiz.getFileName();
        System.out.println("ファイル名：" + fileName);
        if(fileName == null){ // アップロードファイルが無いクイズの場合
            return "quiz/edit";
        }

        String base64data = ConvertToBase64(uploadDirPath + fileName);

        model.addAttribute("base64data", base64data);

        return "quiz/edit";
    }


    private Quiz makeQuiz(QuizForm quizForm, int quizId){
        Quiz quiz = new Quiz();
        if(quizId != 0){ // 新規作成の場合
            quiz.setId(quizId);
        }
        quiz.setQuestion(quizForm.getQuestion());
        quiz.setChoice1(quizForm.getChoice1());
        quiz.setChoice2(quizForm.getChoice2());
        quiz.setChoice3(quizForm.getChoice3());
        quiz.setChoice4(quizForm.getChoice4());
        quiz.setAnswer(quizForm.getAnswer());
        quiz.setFileName(quizForm.getFileName());

        MultipartFile uploadFile = quizForm.getUploadFile();
        if(!uploadFile.isEmpty()){ // アップロードファイルがある場合
            try{
                String fileName = uploadFile.getOriginalFilename();
                byte[] bytes = uploadFile.getBytes();
                BufferedOutputStream uploadFileStream = new BufferedOutputStream(
                                                            new FileOutputStream(
                                                                new File(uploadDirPath + fileName)));
                uploadFileStream.write(bytes); // 同一ファイル名がある場合は上書きされるので注意！！！
                uploadFileStream.close();

                // fileNameの登録（逆に、アップロードファイルが無い場合はsetFileNameが実行されないので、既存の画像が残る）
                quiz.setFileName(fileName);

                System.out.println(fileName + "のアップロードが完了しました");
            } catch(Exception e) {
                System.out.println("ファイルアップロードでエラー発生（Exception）");
                e.printStackTrace();
            } catch(Throwable t) {
                System.out.println("ファイルアップロードでエラー発生（Throwable）");
                t.printStackTrace();
            }
        }

        return quiz;
    }

    // quizエンティティにはMultipartFileのフィールドを設けていないため、makeQuizFormの方にはアップロードファイル関連の処理は無い
    // （base64への変換とmodelへのaddはeditメソッドの方でやってる）
    // （quizFormにbase64のフィールド作っても、return "quiz/edit";辺りの処理がややこしくなるのでこれで良い）
    private QuizForm makeQuizForm(Quiz quiz){
        QuizForm quizForm = new QuizForm();
        quizForm.setId(quiz.getId());
        quizForm.setQuestion(quiz.getQuestion());
        quizForm.setChoice1(quiz.getChoice1());
        quizForm.setChoice2(quiz.getChoice2());
        quizForm.setChoice3(quiz.getChoice3());
        quizForm.setChoice4(quiz.getChoice4());
        quizForm.setAnswer(quiz.getAnswer());
        quizForm.setFileName(quiz.getFileName());

        return quizForm;
    }

    private String ConvertToBase64(String filePath){
        File uploadFile = new File(filePath);
        
        StringBuilder sb = new StringBuilder();
        try { // 画像表示のためにbase64エンコードする
            byte[] data = Files.readAllBytes(uploadFile.toPath());
            String contentType = Files.probeContentType(uploadFile.toPath());
            System.out.println("コンテンツタイプ：" + contentType);

            String base64str = Base64.getEncoder().encodeToString(data);
            sb.append("data:");
            sb.append(contentType);
            sb.append(";base64,");
            sb.append(base64str);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }
}