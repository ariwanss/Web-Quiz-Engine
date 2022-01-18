package engine.quizCompletion;

import com.fasterxml.jackson.databind.ObjectMapper;
import engine.configuration.UserDetailsServiceImpl;
import engine.quiz.Quiz;
import engine.user.User;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;

import java.util.List;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizCompletionController.class)
class QuizCompletionControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    QuizCompletionService quizCompletionService;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    PasswordEncoder passwordEncoder;

    User user;
    Quiz quiz1;
    Quiz quiz2;
    QuizCompletion quizCompletion1;
    QuizCompletion quizCompletion2;

    @BeforeEach
    void setUp() {
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
    @WithMockUser
    void getCompletedQuizzesByUser() throws Exception {
        List<QuizCompletion> quizCompletionList = List.of(quizCompletion1, quizCompletion2);
        Page<QuizCompletion> quizCompletionPage = new PageImpl<>(quizCompletionList);

        Mockito.when(quizCompletionService.getCompletedQuizzes(0)).thenReturn(quizCompletionPage);

        mockMvc.perform(
                get("/api/quizzes/completed")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(quizCompletionPage)));
    }

    @Test
    void getCompletedQuizzesByUserUnauthorized() throws Exception {
        mockMvc.perform(
                        get("/api/quizzes/completed")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }
}