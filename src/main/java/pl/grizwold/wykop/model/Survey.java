package pl.grizwold.wykop.model;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Survey {
    private String question;
    private List<String> answers;

    public Survey(String question, String... answers) {
        this.question = question;
        this.answers = Arrays.asList(answers);
    }

    @Override
    public String toString() {
        return Stream.concat(
                Stream.of(question),
                answers.stream()
        ).collect(Collectors.joining(","));
    }
}
