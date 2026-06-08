package quize.application.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import quize.application.demo.model.*;
import quize.application.demo.security.CustomUserDetails;
import quize.application.demo.service.QuizResultService;
import quize.application.demo.service.QuizService;
import quize.application.demo.service.UserService;

import java.util.List;

@Controller
@RequestMapping("/quiz")
public class QuizController {

    private final QuizService quizService;
    private final QuizResultService quizResultService;
    private final UserService userService;

    public QuizController(QuizService quizService, QuizResultService quizResultService, UserService userService) {
        this.quizService = quizService;
        this.quizResultService = quizResultService;
        this.userService = userService;
    }

    @GetMapping("/list")
    public String listQuizzes(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        model.addAttribute("quizzes", quizService.findAllQuizzes());
        model.addAttribute("results", quizResultService.getResultsByUser(user));
        return "quiz/list";
    }

    @GetMapping("/history")
    public String quizHistory(Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        User user = userDetails.getUser();
        List<QuizResult> results = quizResultService.getResultsByUser(user);
        model.addAttribute("results", results);
        model.addAttribute("totalQuizzes", results.size());
        if (!results.isEmpty()) {
            double avgPercentage = results.stream().mapToDouble(QuizResult::getPercentage).average().orElse(0);
            int bestScore = results.stream().mapToInt(QuizResult::getScore).max().orElse(0);
            int totalCorrect = results.stream().mapToInt(QuizResult::getScore).sum();
            int totalAttempted = results.stream().mapToInt(QuizResult::getTotalQuestions).sum();
            model.addAttribute("avgPercentage", avgPercentage);
            model.addAttribute("bestScore", bestScore);
            model.addAttribute("totalCorrect", totalCorrect);
            model.addAttribute("totalAttempted", totalAttempted);
        }
        return "quiz/history";
    }

    @GetMapping("/{id}")
    public String viewQuiz(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Quiz quiz = quizService.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        List<Question> questions = quizService.findQuestionsByQuizId(id);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questionCount", questions.size());
        if (userDetails != null) {
            User user = userDetails.getUser();
            model.addAttribute("hasTaken", quizResultService.hasUserTakenQuiz(user, quiz));
        }
        return "quiz/detail";
    }

    @GetMapping("/{id}/take")
    public String takeQuiz(@PathVariable Long id, Model model, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Quiz quiz = quizService.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        User user = userDetails.getUser();

        if (quizResultService.hasUserTakenQuiz(user, quiz)) {
            return "redirect:/quiz/" + id;
        }

        List<Question> questions = quizService.findQuestionsByQuizId(id);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);
        return "quiz/take";
    }

    @PostMapping("/{id}/submit")
    public String submitQuiz(@PathVariable Long id,
                             @RequestParam List<String> answers,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             Model model) {
        Quiz quiz = quizService.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        User user = userService.findByUsername(userDetails.getUser().getUsername()).orElseThrow();

        List<Question> questions = quizService.findQuestionsByQuizId(id);
        List<String> correctAnswers = questions.stream().map(Question::getCorrectAnswer).toList();

        System.out.println("=== Quiz Submission Debug ===");
        System.out.println("Quiz ID: " + id);
        System.out.println("User Answers received: " + answers);
        System.out.println("Correct Answers: " + correctAnswers);
        System.out.println("Number of user answers: " + answers.size());
        System.out.println("Number of questions: " + questions.size());

        int score = quizResultService.calculateScore(answers, correctAnswers);
        System.out.println("Calculated Score: " + score + "/" + questions.size());
        System.out.println("===========================");

        QuizResult result = new QuizResult(user, quiz, score, questions.size());
        quizResultService.saveResult(result);

        model.addAttribute("quiz", quiz);
        model.addAttribute("result", result);
        model.addAttribute("questions", questions);
        model.addAttribute("userAnswers", answers);
        model.addAttribute("correctAnswers", correctAnswers);
        return "quiz/result";
    }
}
