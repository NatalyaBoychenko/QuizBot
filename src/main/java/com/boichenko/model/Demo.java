package com.boichenko.model;

import com.boichenko.QuizBot;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

public class Demo {
    public static void main(String[] args) throws TelegramApiException {

        ToNewBranch newBr = new ToNewBranch();
        TelegramBotsApi telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        telegramBotsApi.registerBot(new QuizBot());

    }
}
