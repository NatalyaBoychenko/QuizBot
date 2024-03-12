package com.boichenko.model;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class QuizResponseApiDto {
    private Long id;
    private String question;
    private String description;
    private Map<String, String> answers;
    @JsonProperty(value = "multiple_correct_answers")
    private String multipleCorrectAnswers;
    @JsonProperty(value = "correct_answers")
    private Map<String, String> correctAnswers;
    private String explanation;
    private String tips;
    private List<Tag> tags;
    private String category;
    private String difficulty;
}
