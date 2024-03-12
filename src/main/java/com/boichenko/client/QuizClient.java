package com.boichenko.client;

import com.boichenko.model.QuizResponseApiDto;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.List;

import static com.boichenko.BotConstants.*;

public class QuizClient {
    private HttpClient httpClient = HttpClient.newHttpClient();
    private ObjectMapper objectMapper = new ObjectMapper();

    @SneakyThrows
    public List<QuizResponseApiDto> getDefaultQuiz() {
        HttpRequest request = HttpRequest.newBuilder(new URI(QUIZ_URL))
                .GET()
                .header(QUIZ_AUTH_HEADER, QUIZ_TOKEN)
                .build();
        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        return objectMapper.readValue(response.body(), new TypeReference<List<QuizResponseApiDto>>() {
        });
    }
}
