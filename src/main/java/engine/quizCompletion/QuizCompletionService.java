package engine.quizCompletion;

import engine.configuration.AuditorAwareImpl;
import engine.quiz.Quiz;
import engine.user.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.*;
import org.springframework.stereotype.Service;

@Service
public class QuizCompletionService {

    private final QuizCompletionRepository quizCompletionRepository;
    private final AuditorAware<User> auditorProvider;

    public QuizCompletionService(QuizCompletionRepository quizCompletionRepository, AuditorAware<User> auditorProvider) {
        this.quizCompletionRepository = quizCompletionRepository;
        this.auditorProvider = auditorProvider;
    }

    public void save(Quiz quiz) {
        QuizCompletion quizCompletion = new QuizCompletion();
        //quizCompletion.setUser(auditorProvider.getCurrentAuditor().get());
        quizCompletion.setQuiz(quiz);
        //quizCompletion.setQuizIdNumber(quiz.getId());
        quizCompletionRepository.save(quizCompletion);
    }

    public Page<QuizCompletion> getCompletedQuizzes(int page) {
        Pageable paging = PageRequest.of(page, 10, Sort.by("completedAt").descending());
        return quizCompletionRepository.findByUser(auditorProvider.getCurrentAuditor().get(), paging);
    }
}
