package engine.quiz;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import engine.configuration.QuizOwnerEvaluator;
import engine.configuration.UserDetailsServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.web.server.ResponseStatusException;

import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@WebMvcTest(QuizController.class)
class QuizControllerUnitTest {

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @MockBean
    QuizService quizService;

    @MockBean
    UserDetailsServiceImpl userDetailsService;

    @MockBean
    PasswordEncoder passwordEncoder;

    @MockBean(name = "quizOwnerEvaluator")
    QuizOwnerEvaluator quizOwnerEvaluator;

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
        initEntities();
    }

    @Test
    @WithMockUser
    void getQuizOk() throws Exception {
        Mockito.when(quizService.getQuiz(1)).thenReturn(quiz1);

        mockMvc.perform(
                        get("/api/quizzes/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(quiz1)));
    }

    @Test
    @WithMockUser
    void getQuizNotFound() throws Exception {
        Mockito.when(quizService.getQuiz(1)).thenThrow(new ResponseStatusException(HttpStatus.NOT_FOUND));

        mockMvc.perform(
                        get("/api/quizzes/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isNotFound());
    }

    @Test
    void getQuizUnauthorized() throws Exception {
        mockMvc.perform(
                        get("/api/quizzes/1")
                                .contentType(MediaType.APPLICATION_JSON)
                )

                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void getAllQuizzesOk() throws Exception {
        Page<Quiz> quizPage = new PageImpl<>(List.of(quiz1, quiz2));

        Mockito.when(quizService.getAllQuizzes(0)).thenReturn(quizPage);

        mockMvc.perform(
                get("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(quizPage)));
    }

    @Test
    void getAllQuizzesUnauthorized() throws Exception {
        mockMvc.perform(
                        get("/api/quizzes")
                                .contentType(MediaType.APPLICATION_JSON)
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void solveQuizOkCorrect() throws Exception {
        Mockito.when(quizService.solveQuiz(1, new int[]{2})).thenReturn(successMessage);

        mockMvc.perform(
                post("/api/quizzes/1/solve")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(Map.of("answer", new int[]{2})))
        )
                .andExpect(status().isOk())
                .andExpect(content().string(successMessage));
    }

    @Test
    @WithMockUser
    void solveQuizOkIncorrect() throws Exception {
        Mockito.when(quizService.solveQuiz(1, new int[]{1})).thenReturn(failMessage);

        mockMvc.perform(
                        post("/api/quizzes/1/solve")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("answer", new int[]{1})))
                )
                .andExpect(status().isOk())
                .andExpect(content().string(failMessage));
    }

    @Test
    void solveQuizUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/api/quizzes/1/solve")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(Map.of("answer", new int[]{2})))
                )
                .andExpect(status().isUnauthorized());
    }

    @Test
    @WithMockUser
    void newQuizOk() throws Exception {
        Mockito.when(quizService.newQuiz(quiz1)).thenReturn(quiz1.copy());

        mockMvc.perform(
                post("/api/quizzes")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(quiz1))
        )
                .andExpect(status().isOk())
                .andExpect(content().string(objectMapper.writeValueAsString(quiz1.copy())));
    }

    @Test
    void newQuizUnauthorized() throws Exception {
        mockMvc.perform(
                        post("/api/quizzes")
                                .contentType(MediaType.APPLICATION_JSON)
                                .content(objectMapper.writeValueAsString(quiz1))
                )
                .andExpect(status().isUnauthorized());
    }

    /*@Test
    @WithMockUser
    void deleteQuizOk() throws Exception {
        Mockito.when(quizOwnerEvaluator.evaluate(eq(1), any())).thenReturn(true);
        Mockito.doNothing().when(quizService).deleteQuiz(1);

        mockMvc.perform(
                delete("/api/quizzes/1")
                        .contentType(MediaType.APPLICATION_JSON)
        )
                .andExpect(status().isOk());
    }*/
}