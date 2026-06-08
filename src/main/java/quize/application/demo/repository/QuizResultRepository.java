package quize.application.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quize.application.demo.model.QuizResult;
import quize.application.demo.model.User;
import quize.application.demo.model.Quiz;

import java.util.List;

@Repository
public interface QuizResultRepository extends JpaRepository<QuizResult, Long> {

    List<QuizResult> findByUserOrderByCompletedAtDesc(User user);

    List<QuizResult> findByQuizOrderByCompletedAtDesc(Quiz quiz);

    boolean existsByUserAndQuiz(User user, Quiz quiz);

    List<QuizResult> findAllByOrderByCompletedAtDesc();
}
