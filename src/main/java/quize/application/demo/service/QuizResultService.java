package quize.application.demo.service;

import org.springframework.stereotype.Service;
import quize.application.demo.model.Quiz;
import quize.application.demo.model.QuizResult;
import quize.application.demo.model.User;
import quize.application.demo.repository.QuizResultRepository;

import java.util.List;

@Service
public class QuizResultService {

    private final QuizResultRepository quizResultRepository;

    public QuizResultService(QuizResultRepository quizResultRepository) {
        this.quizResultRepository = quizResultRepository;
    }

    public QuizResult saveResult(QuizResult result) {
        return quizResultRepository.save(result);
    }

    public List<QuizResult> getResultsByUser(User user) {
        return quizResultRepository.findByUserOrderByCompletedAtDesc(user);
    }

    public List<QuizResult> getResultsByQuiz(Quiz quiz) {
        return quizResultRepository.findByQuizOrderByCompletedAtDesc(quiz);
    }

    public boolean hasUserTakenQuiz(User user, Quiz quiz) {
        return quizResultRepository.existsByUserAndQuiz(user, quiz);
    }

    public int calculateScore(List<String> userAnswers, List<String> correctAnswers) {
        int score = 0;
        for (int i = 0; i < userAnswers.size(); i++) {
            if (i < correctAnswers.size() && userAnswers.get(i) != null && userAnswers.get(i).equals(correctAnswers.get(i))) {
                score++;
            }
        }
        return score;
    }

    public List<QuizResult> getAllResults() {
        return quizResultRepository.findAllByOrderByCompletedAtDesc();
    }

    public long countResults() {
        return quizResultRepository.count();
    }
}
