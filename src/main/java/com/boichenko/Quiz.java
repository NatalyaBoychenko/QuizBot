package com.boichenko;

import com.boichenko.model.QuizResponseApiDto;
import lombok.Data;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

@Data
public class Quiz {

    private List<QuizResponseApiDto> quizList;
    private AtomicInteger count = new AtomicInteger();
    private String lastCorrectAnswer;

    public Quiz(List<QuizResponseApiDto> quizList) {
        this.quizList = quizList;
    }
}
