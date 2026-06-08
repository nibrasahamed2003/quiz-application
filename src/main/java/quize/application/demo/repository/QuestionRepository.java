package quize.application.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quize.application.demo.model.Question;
import quize.application.demo.model.Quiz;

import java.util.List;

@Repository
public interface QuestionRepository extends JpaRepository<Question, Long> {

    List<Question> findByQuizOrderByQuizId(Quiz quiz);

    List<Question> findByQuizId(Long quizId);
}
