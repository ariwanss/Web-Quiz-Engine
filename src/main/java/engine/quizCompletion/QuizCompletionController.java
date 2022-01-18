package engine.quizCompletion;

import org.springframework.data.domain.Page;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class QuizCompletionController {

    private final QuizCompletionService quizCompletionService;

    public QuizCompletionController(QuizCompletionService quizCompletionService) {
        this.quizCompletionService = quizCompletionService;
    }

    @GetMapping("/api/quizzes/completed")
    public ResponseEntity<Page<QuizCompletion>> getCompletedQuizzes(
            @RequestParam(defaultValue = "0") int page
    ) {
        return ResponseEntity.ok().body(quizCompletionService.getCompletedQuizzes(page));
    }
}
