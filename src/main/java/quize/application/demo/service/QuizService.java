package quize.application.demo.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import quize.application.demo.model.Question;
import quize.application.demo.model.Quiz;
import quize.application.demo.model.User;
import quize.application.demo.repository.QuestionRepository;
import quize.application.demo.repository.QuizRepository;

import java.util.List;
import java.util.Optional;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;

    public QuizService(QuizRepository quizRepository, QuestionRepository questionRepository) {
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
    }

    @Transactional
    public Quiz createQuiz(Quiz quiz, List<Question> questions) {
        Quiz savedQuiz = quizRepository.save(quiz);
        for (Question question : questions) {
            question.setQuiz(savedQuiz);
            questionRepository.save(question);
        }
        return savedQuiz;
    }

    public List<Quiz> findAllQuizzes() {
        return quizRepository.findAllByOrderByCreatedAtDesc();
    }

    public List<Quiz> findQuizzesByUser(User user) {
        return quizRepository.findByCreatedByOrderByCreatedAtDesc(user);
    }

    public Optional<Quiz> findById(Long id) {
        return quizRepository.findById(id);
    }

    public List<Question> findQuestionsByQuizId(Long quizId) {
        return questionRepository.findByQuizId(quizId);
    }

    @Transactional
    public void deleteQuiz(Long id) {
        quizRepository.deleteById(id);
    }

    public long countQuizzes() {
        return quizRepository.count();
    }
}
