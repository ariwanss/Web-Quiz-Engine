package engine.quizCompletion;

import engine.quiz.Quiz;
import engine.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.data.domain.*;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;

class QuizCompletionServiceUnitTest {

    @Mock
    QuizCompletionRepository quizCompletionRepository;

    @Mock
    AuditorAware<User> auditorProvider;

    @InjectMocks
    QuizCompletionService quizCompletionService;

    User user;
    Quiz quiz1;
    Quiz quiz2;
    QuizCompletion quizCompletion1;
    QuizCompletion quizCompletion2;


    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        initEntities();
    }

    void initEntities() {
        user = new User("someone@gmail.com", "password123");

        quiz1 = new Quiz(

                "The Java Logo",
                "What is depicted in the Java logo?",
                new String[]{"Robot", "Tea leaf", "Cup of coffee", "Bub"},
                new int[2]
        );

        quizCompletion1 = new QuizCompletion();
        quizCompletion1.setQuiz(quiz1);
        quizCompletion1.setUser(user);

        quiz2 = new Quiz(
                "The Ultimate Question",
                "What is the answer to the Ultimate Question of Life, the Universe and Everything?",
                new String[]{"Everything goes right", "42", "2+2=4", "11011100"},
                new int[1]
        );

        quizCompletion2 = new QuizCompletion();
        quizCompletion2.setQuiz(quiz2);
        quizCompletion2.setUser(user);

    }

    @Test
    void saveQuizCompletion() {
        ArgumentCaptor<QuizCompletion> quizCompletionArgument = ArgumentCaptor.forClass(QuizCompletion.class);
        quizCompletionService.save(quiz1);
        Mockito.verify(quizCompletionRepository).save(quizCompletionArgument.capture());
        QuizCompletion capturedQuizCompletion = quizCompletionArgument.getValue();
        assertEquals(quizCompletion1.getQuiz(), capturedQuizCompletion.getQuiz());
    }

    @Test
    void getCompletedQuizByUser() {
        Pageable paging = PageRequest.of(0, 10, Sort.by("completedAt").descending());
        List<QuizCompletion> quizCompletionList = List.of(quizCompletion1, quizCompletion2);
        Page<QuizCompletion> quizCompletionPage = new PageImpl<>(quizCompletionList);

        Mockito.when(auditorProvider.getCurrentAuditor()).thenReturn(Optional.of(user));
        Mockito.when(quizCompletionRepository.findByUser(user, paging))
                .thenReturn(quizCompletionPage);

        assertEquals(quizCompletionList, quizCompletionService.getCompletedQuizzes(0).getContent());
    }
}