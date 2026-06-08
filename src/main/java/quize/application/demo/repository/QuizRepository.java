package quize.application.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import quize.application.demo.model.Quiz;
import quize.application.demo.model.User;

import java.util.List;

@Repository
public interface QuizRepository extends JpaRepository<Quiz, Long> {

    List<Quiz> findByCreatedByOrderByCreatedAtDesc(User createdBy);

    List<Quiz> findAllByOrderByCreatedAtDesc();
}
