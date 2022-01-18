package engine.quiz;

import engine.quizCompletion.QuizCompletionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.util.*;

@Service
public class QuizService {

    private final QuizRepository quizRepository;
    private final QuizCompletionService quizCompletionService;

    public QuizService(QuizRepository quizRepository, QuizCompletionService quizCompletionService) {
        this.quizRepository = quizRepository;
        this.quizCompletionService = quizCompletionService;
    }

    public Quiz getQuiz(long id) {
        return quizRepository.findById(id).orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND));
    }

    public Quiz newQuiz(Quiz quiz) {
        quiz = quiz.copy();
        return quizRepository.save(quiz);
    }

    public List<Quiz> getAllQuizzes() {
        return (List<Quiz>) quizRepository.findAll();
    }

    public Page<Quiz> getAllQuizzes(int page) {
        Pageable paging = PageRequest.of(page, 10);
        return quizRepository.findAll(paging);
    }

    public String solveQuiz(long id, int[] answer) {
        Quiz quiz = getQuiz(id);
        int[] quizAnswer = quiz.getAnswer();
        int[] userAnswer = Objects.requireNonNullElseGet(answer, () -> new int[0]);

        if (Arrays.equals(quizAnswer, userAnswer)) {
            quizCompletionService.save(quiz);
            return "{\"success\":true,\"feedback\":\"Congratulations, you're right!\"}";
        } else {
            return "{\"success\":false,\"feedback\":\"Wrong answer! Please, try again.\"}";
        }
    }

    public void deleteQuiz(long id) {
        if (quizRepository.existsById(id)) {
            quizRepository.deleteById(id);
        } else {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND);
        }
    }
}
