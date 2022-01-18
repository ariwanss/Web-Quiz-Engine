package engine.user;

import engine.quiz.Quiz;
import engine.quizCompletion.QuizCompletion;
import lombok.*;

import javax.persistence.*;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.util.List;

@Entity
@Table
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class User {

    @Id
    @GeneratedValue
    private long id;

    @Pattern(regexp = ".+@.+\\..+")
    @NotBlank
    private String email;

    @Size(min = 5)
    @NotBlank
    private String password;

    @Column(columnDefinition = "varchar(12) default 'ROLE_USER'")
    private String role;

    @OneToMany(mappedBy = "author", cascade = CascadeType.ALL)
    private List<Quiz> quizzes;

    @OneToMany(mappedBy = "user", cascade = CascadeType.ALL)
    private List<QuizCompletion> quizCompletions;

    public User(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        User user = (User) o;

        return id == user.id;
    }

    @Override
    public int hashCode() {
        return (int) (id ^ (id >>> 32));
    }
}
