package engine.quiz;

import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.Map;

@RestController
@Validated
public class QuizController {

    private final QuizService quizService;

    public QuizController(QuizService quizService) {
        this.quizService = quizService;
    }

    @GetMapping("/api/quizzes/{id}")
    public ResponseEntity<Quiz> getQuiz(@PathVariable long id) {
        return ResponseEntity.ok().body(quizService.getQuiz(id));
    }

    @GetMapping("/api/quizzes")
    public ResponseEntity<Page<Quiz>> getAllQuizzes(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok().body(quizService.getAllQuizzes(page));
    }

    @PostMapping("/api/quizzes/{id}/solve")
    public ResponseEntity<String> solveQuiz(@PathVariable long id, @RequestBody Map<String, int[]> answer) {
        return ResponseEntity.ok().body(quizService.solveQuiz(id, answer.get("answer")));
    }

    @PostMapping("/api/quizzes")
    public ResponseEntity<Quiz> newQuiz(@Valid @RequestBody Quiz quiz) {
        return ResponseEntity.ok().body(quizService.newQuiz(quiz));
    }

    @DeleteMapping("/api/quizzes/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    @PreAuthorize("@quizOwnerEvaluator.evaluate(#id, principal)")
    public void deleteQuiz(@PathVariable long id) {
        quizService.deleteQuiz(id);
    }
}
