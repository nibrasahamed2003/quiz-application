package quize.application.demo.controller;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import quize.application.demo.model.*;
import quize.application.demo.security.CustomUserDetails;
import quize.application.demo.service.QuizResultService;
import quize.application.demo.service.QuizService;
import quize.application.demo.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final UserService userService;
    private final QuizService quizService;
    private final QuizResultService quizResultService;

    public AdminController(UserService userService, QuizService quizService, QuizResultService quizResultService) {
        this.userService = userService;
        this.quizService = quizService;
        this.quizResultService = quizResultService;
    }

    @GetMapping("/dashboard")
    public String dashboard(Model model) {
        model.addAttribute("totalUsers", userService.countUsers());
        model.addAttribute("totalQuizzes", quizService.countQuizzes());
        model.addAttribute("totalResults", quizResultService.countResults());
        model.addAttribute("users", userService.findAllUsers());
        model.addAttribute("quizzes", quizService.findAllQuizzes());
        model.addAttribute("results", quizResultService.getAllResults());
        return "admin/dashboard";
    }

    // ---- Quiz Management ----

    @GetMapping("/quiz/create")
    public String showCreateQuizForm(Model model) {
        model.addAttribute("quiz", new Quiz());
        return "admin/quiz-create";
    }

    @PostMapping("/quiz/create")
    public String createQuiz(@RequestParam String title,
                             @RequestParam String description,
                             @RequestParam List<String> questionText,
                             @RequestParam List<String> optionA,
                             @RequestParam List<String> optionB,
                             @RequestParam List<String> optionC,
                             @RequestParam List<String> optionD,
                             @RequestParam List<String> correctAnswer,
                             @AuthenticationPrincipal CustomUserDetails userDetails,
                             RedirectAttributes redirectAttributes) {
        try {
            User user = userService.findByUsername(userDetails.getUser().getUsername()).orElseThrow();
            Quiz quiz = new Quiz(title, description, user);

            List<Question> questions = new ArrayList<>();
            for (int i = 0; i < questionText.size(); i++) {
                if (!questionText.get(i).isBlank()) {
                    Question q = new Question(
                            questionText.get(i),
                            optionA.get(i),
                            optionB.get(i),
                            optionC.get(i),
                            optionD.get(i),
                            correctAnswer.get(i)
                    );
                    questions.add(q);
                }
            }

            quizService.createQuiz(quiz, questions);
            redirectAttributes.addFlashAttribute("success", "Quiz created successfully!");
            return "redirect:/admin/dashboard";
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("error", "Error creating quiz: " + e.getMessage());
            return "redirect:/admin/quiz/create";
        }
    }

    @PostMapping("/quiz/{id}/delete")
    public String deleteQuiz(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        quizService.deleteQuiz(id);
        redirectAttributes.addFlashAttribute("success", "Quiz deleted successfully!");
        return "redirect:/admin/dashboard";
    }

    // ---- User Management ----

    @PostMapping("/user/{id}/delete")
    public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        userService.deleteUser(id);
        redirectAttributes.addFlashAttribute("success", "User deleted successfully!");
        return "redirect:/admin/dashboard";
    }

    // ---- View Quiz Details ----

    @GetMapping("/quiz/{id}")
    public String viewQuizDetails(@PathVariable Long id, Model model) {
        Quiz quiz = quizService.findById(id).orElseThrow(() -> new RuntimeException("Quiz not found"));
        List<Question> questions = quizService.findQuestionsByQuizId(id);
        List<QuizResult> quizResults = quizResultService.getResultsByQuiz(quiz);
        model.addAttribute("quiz", quiz);
        model.addAttribute("questions", questions);
        model.addAttribute("quizResults", quizResults);
        return "admin/quiz-detail";
    }

    // ---- All Results ----

    @GetMapping("/results")
    public String allResults(Model model) {
        model.addAttribute("results", quizResultService.getAllResults());
        return "admin/results";
    }
}
