package com.boichenko;

import com.boichenko.client.QuizClient;
import com.boichenko.model.QuizResponseApiDto;
import lombok.SneakyThrows;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Contact;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboard;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.buttons.KeyboardRow;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import static com.boichenko.BotConstants.BEGIN;
import static com.boichenko.BotConstants.START;

public class QuizBot extends TelegramLongPollingBot {
    private QuizClient quizClient = new QuizClient();
    private Map<String, Quiz> context = new ConcurrentHashMap<>();


    @SneakyThrows
    @Override
    public void onUpdateReceived(Update update) {
        SendMessage message = new SendMessage();
        String chatId = update.getMessage().getChatId().toString();
        message.setChatId(chatId);

        if (isMessage(update) && update.getMessage().getText().equalsIgnoreCase(START)) {
            List<QuizResponseApiDto> defaultQuiz = quizClient.getDefaultQuiz();
            context.put(chatId, new Quiz(defaultQuiz));
            message.setText(String.format("Quiz is ready and contains %s questions. Press %s to start", defaultQuiz.size(), BEGIN));
            message.setReplyMarkup(setupBeginButton());
        } else if (isMessage(update) && update.getMessage().getText().equalsIgnoreCase(BEGIN)) {
            sendNextMessage(chatId, message);
        } else {
            String currentAnswer = update.getMessage().getText();
            Quiz quiz = context.get(chatId);
            if (currentAnswer.equalsIgnoreCase(quiz.getLastCorrectAnswer())){
                sendNextMessage(chatId, message);
            } else {
                message.setText("Incorrect answer. Please try again");
            }
        }

        try {
            execute(message); // Call method to send the message
        } catch (TelegramApiException e) {
            e.printStackTrace();
        }
    }

    private void logUser(Contact contact) {
        System.out.println(contact.getFirstName());
    }


    private static boolean isMessage(Update update) {
        return update.hasMessage() && update.getMessage().hasText();
    }

    @Override
    public String getBotUsername() {
        return BotConstants.BOT_NAME;
    }

    @Override
    public String getBotToken() {
        // TODO
        return BotConstants.BOT_TOKEN;
    }

    private ReplyKeyboard setupBeginButton() {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        KeyboardRow row = new KeyboardRow();
        row.add(BEGIN);
        keyboard.setKeyboard(List.of(row));
        return keyboard;
    }

    private ReplyKeyboard setupQuizKeyboard(QuizResponseApiDto quizResponseApiDto) {
        ReplyKeyboardMarkup keyboard = new ReplyKeyboardMarkup();
        List<KeyboardRow> rows = quizResponseApiDto.getAnswers().entrySet()
                .stream()
                .filter(entry -> entry.getValue() != null)
                .map(entry ->{
                    KeyboardRow row = new KeyboardRow();
                    row.add(entry.getValue());
                    return row;
                })
                .toList();
        keyboard.setKeyboard(rows);

        return keyboard;
    }

    private String findCorrectAnswers(Map<String, String> correctAnswers) {
        String result = correctAnswers.entrySet().stream()
                .filter(entry -> entry.getValue().equals("true"))
                .map(Map.Entry::getKey)
                .findFirst()
                .orElseThrow();

        return result.replace("_correct", "");
    }

    private void sendNextMessage(String chatId, SendMessage message) {
        Quiz quiz = context.get(chatId);
        List<QuizResponseApiDto> quizList = quiz.getQuizList();
        AtomicInteger counter = quiz.getCount();
        if (quizList.size() > counter.get()) {
            QuizResponseApiDto quizResponseApiDto = quizList.get(counter.getAndAdd(1));
            message.setText(quizResponseApiDto.getQuestion());
            message.setReplyMarkup(setupQuizKeyboard(quizResponseApiDto));
            String correctAnswerKey = findCorrectAnswers(quizResponseApiDto.getCorrectAnswers());
            quiz.setLastCorrectAnswer(quizResponseApiDto.getAnswers().get(correctAnswerKey));
        } else {
            message.setText("Quiz is completed");
            quiz.setLastCorrectAnswer(null);
        }
    }
}


