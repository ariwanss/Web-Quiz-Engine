package engine.quizCompletion;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonSerialize;
import engine.quiz.Quiz;
import engine.user.User;
import lombok.*;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@EntityListeners(AuditingEntityListener.class)
//@JsonSerialize(using = QuizCompletionSerializer.class)
public class QuizCompletion {

    @Id
    @GeneratedValue
    @JsonIgnore
    private long id;

    @ManyToOne
    @JoinColumn(name = "userId")
    @JsonIgnore
    @CreatedBy
    private User user;

    @ManyToOne
    @JoinColumn(name = "quizId")
    @JsonIgnore
    private Quiz quiz;

    @JsonProperty("id")
    @Transient
    private long quizIdNumber;

    @CreatedDate
    private LocalDateTime completedAt;

    /*public void setQuizIdNumber(long quizIdNumber) {
        this.quizIdNumber = quiz.getId();
    }*/

    public long getQuizIdNumber() {
        return quiz.getId();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        QuizCompletion that = (QuizCompletion) o;

        return id == that.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
