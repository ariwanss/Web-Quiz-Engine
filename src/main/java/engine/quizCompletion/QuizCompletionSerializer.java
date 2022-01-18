package engine.quizCompletion;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.std.StdSerializer;
import engine.quiz.Quiz;

import java.io.IOException;

public class QuizCompletionSerializer extends StdSerializer<QuizCompletion> {

    protected QuizCompletionSerializer(Class<QuizCompletion> t) {
        super(t);
    }

    @Override
    public void serialize(QuizCompletion value, JsonGenerator gen, SerializerProvider provider) throws IOException {
        gen.writeStartObject();
        gen.writeNumberField("id", value.getQuiz().getId());
        gen.writeObjectField("completedAt", value.getCompletedAt());
        gen.writeEndObject();
    }
}
