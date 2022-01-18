package engine.configuration;

import engine.quiz.QuizService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component(value = "quizOwnerEvaluator")
public class QuizOwnerEvaluator {

    private final QuizService quizService;

    public QuizOwnerEvaluator(QuizService quizService) {
        this.quizService = quizService;
    }

    public boolean evaluate(long id, UserDetailsImpl userDetails) {
        return Objects.equals(quizService.getQuiz(id).getAuthor().getEmail(), userDetails.getUsername());
    }
}
