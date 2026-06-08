package quize.application.demo.config;

import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import quize.application.demo.model.Question;
import quize.application.demo.model.Quiz;
import quize.application.demo.model.User;
import quize.application.demo.repository.QuestionRepository;
import quize.application.demo.repository.QuizRepository;
import quize.application.demo.repository.UserRepository;

@Component
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final QuizRepository quizRepository;
    private final QuestionRepository questionRepository;
    private final PasswordEncoder passwordEncoder;

    public DataSeeder(UserRepository userRepository, QuizRepository quizRepository,
                      QuestionRepository questionRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.quizRepository = quizRepository;
        this.questionRepository = questionRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(String... args) {
        // Update admin user if exists with old credentials
        userRepository.findByUsername("admin").ifPresent(admin -> {
            admin.setUsername("adminnibras");
            admin.setPassword(passwordEncoder.encode("passnibras"));
            userRepository.save(admin);
            System.out.println("=== Admin credentials updated to: adminnibras / passnibras ===");
        });

        // Seed admin user if not exists
        if (userRepository.count() == 0) {
            // Create admin user
            User admin = new User("adminnibras", "admin@quizapp.com",
                    passwordEncoder.encode("passnibras"), User.Role.ADMIN);
            userRepository.save(admin);

            // Create regular user
            User user = new User("user", "user@quizapp.com",
                    passwordEncoder.encode("user123"), User.Role.USER);
            userRepository.save(user);

            // Create sample quiz
            Quiz sampleQuiz = new Quiz(
                    "General Knowledge Quiz",
                    "Test your general knowledge with this fun quiz covering science, geography, and more!",
                    admin
            );
            quizRepository.save(sampleQuiz);

            // Create sample questions
            createQuestion(sampleQuiz,
                    "What is the capital of France?",
                    "London", "Paris", "Berlin", "Madrid", "B");

            createQuestion(sampleQuiz,
                    "Which planet is known as the Red Planet?",
                    "Venus", "Jupiter", "Mars", "Saturn", "C");

            createQuestion(sampleQuiz,
                    "What is the largest ocean on Earth?",
                    "Atlantic Ocean", "Indian Ocean", "Arctic Ocean", "Pacific Ocean", "D");

            createQuestion(sampleQuiz,
                    "Who painted the Mona Lisa?",
                    "Vincent van Gogh", "Leonardo da Vinci", "Pablo Picasso", "Claude Monet", "B");

            createQuestion(sampleQuiz,
                    "What is the chemical symbol for gold?",
                    "Go", "Gd", "Au", "Ag", "C");

            // Create second sample quiz
            Quiz javaQuiz = new Quiz(
                    "Java Programming Basics",
                    "Test your knowledge of Java programming fundamentals!",
                    admin
            );
            quizRepository.save(javaQuiz);

            createQuestion(javaQuiz,
                    "Which keyword is used to create a class in Java?",
                    "struct", "class", "object", "define", "B");

            createQuestion(javaQuiz,
                    "What is the default value of an int variable in Java?",
                    "null", "1", "0", "-1", "C");

            createQuestion(javaQuiz,
                    "Which of these is not a Java primitive type?",
                    "int", "boolean", "String", "char", "C");

            System.out.println("=== Database seeded successfully ===");
            System.out.println("Admin: adminnibras / passnibras");
            System.out.println("User:  user / user123");
        }
    }

    private void createQuestion(Quiz quiz, String questionText,
                                String optionA, String optionB, String optionC,
                                String optionD, String correctAnswer) {
        Question question = new Question(questionText, optionA, optionB, optionC, optionD, correctAnswer);
        question.setQuiz(quiz);
        questionRepository.save(question);
    }
}
