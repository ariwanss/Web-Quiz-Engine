package engine.quiz;

import engine.configuration.UserDetailsServiceImpl;
import engine.quizCompletion.QuizCompletion;
import engine.quizCompletion.QuizCompletionRepository;
import engine.quizCompletion.QuizCompletionService;
import engine.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.web.server.ResponseStatusException;

import javax.persistence.criteria.CriteriaBuilder;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;

class QuizServiceUnitTest {

    @Mock
    QuizRepository quizRepository;

    @Mock
    QuizCompletionService quizCompletionService;

    @InjectMocks
    QuizService quizService;

    Quiz quiz1;
    Quiz quiz2;
    String successMessage = "{\"success\":true,\"feedback\":\"Congratulations, you're right!\"}";
    String failMessage = "{\"success\":false,\"feedback\":\"Wrong answer! Please, try again.\"}";

    void initEntities() {

        quiz1 = new Quiz(
                "The Java Logo",
                "What is depicted in the Java logo?",
                new String[]{"Robot", "Tea leaf", "Cup of coffee", "Bub"},
                new int[]{2}
        );
        quiz1.setId(1);

        quiz2 = new Quiz(
                "The Ultimate Question",
                "What is the answer to the Ultimate Question of Life, the Universe and Everything?",
                new String[]{"Everything goes right", "42", "2+2=4", "11011100"},
                new int[]{1}
        );
        quiz2.setId(2);
    }

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        initEntities();
    }

    @Test
    void getQuiz() {
        Mockito.when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz1));

        assertEquals(quiz1, quizService.getQuiz(1));
    }

    @Test
    void getQuizNotFound() {
        Mockito.when(quizRepository.findById(1L)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        assertThrows(ResponseStatusException.class, () -> quizService.getQuiz(1),
                HttpStatus.NOT_FOUND.getReasonPhrase());
    }

    @Test
    void newQuiz() {
        Mockito.when(quizRepository.save(quiz1.copy())).thenReturn(quiz1.copy());
        assertEquals(quiz1.copy(), quizService.newQuiz(quiz1));
    }

    @Test
    void getAllQuizzesPage() {
        Pageable pageRequest = PageRequest.of(0, 10);
        Page<Quiz> quizPage = new PageImpl<>(List.of(quiz1, quiz2));

        Mockito.when(quizRepository.findAll(pageRequest)).thenReturn(quizPage);

        assertEquals(quizPage, quizService.getAllQuizzes(0));
    }

    @Test
    void solveQuizCorrect() {
        ArgumentCaptor<Quiz> quizArgumentCaptor = ArgumentCaptor.forClass(Quiz.class);
        Mockito.when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz1));
        quizService.solveQuiz(1, new int[]{2});
        Mockito.verify(quizCompletionService).save(quizArgumentCaptor.capture());

        assertEquals(successMessage, quizService.solveQuiz(1, new int[]{2}));
        assertEquals(quiz1, quizArgumentCaptor.getValue());
    }

    @Test
    void solveQuizIncorrect() {
        Mockito.when(quizRepository.findById(1L)).thenReturn(Optional.of(quiz1));

        assertEquals(failMessage, quizService.solveQuiz(1, new int[]{1}));
    }

    @Test
    void deleteQuiz() {
        //ArgumentCaptor<Quiz> quizArgumentCaptor = ArgumentCaptor.forClass(Quiz.class);
        Mockito.when(quizRepository.existsById(1L)).thenReturn(true);
        Mockito.doNothing().when(quizRepository).deleteById(1L);
    }

    @Test
    void deleteQuizNotFound() {
        Mockito.when(quizRepository.existsById(1L)).thenReturn(false);

        assertThrows(ResponseStatusException.class, () -> quizService.deleteQuiz(1),
                HttpStatus.NOT_FOUND.getReasonPhrase());
    }
}