package com.jai_bot_by_tutorial_bot.exchange_rates_bot.bot;
import com.jai_bot_by_tutorial_bot.exchange_rates_bot.exception.ServiceException;

import com.jai_bot_by_tutorial_bot.exchange_rates_bot.sevice.ExchangeRateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;


import java.time.LocalDate;

@Component
public class ExchangeRatesBot extends TelegramLongPollingBot{

    private static final Logger LOG = LoggerFactory.getLogger(ExchangeRatesBot.class);

    private static final String START = "/start";
    private static final String USD = "/usd";
    private static final String KZT = "/kzt";
    private static final String HELP = "/help";

    @Autowired
    private ExchangeRateService exchangeRateService;

    public ExchangeRatesBot(@Value("${bot.token}") String botToken) {
        super(botToken);
    }

    @Override
    public String getBotUsername() {
        return "jai_bot_by_tutorial_bot";
    }

    @Override
    public void onUpdateReceived(Update update) {
    if (!update.hasMessage() || !update.getMessage().hasText()){
        return;
    }
    var message = update.getMessage().getText();
    var chatId = update.getMessage().getChatId();
    switch (message){
        case START -> {
            String userName = update.getMessage().getChat().getUserName();
            startCommand(chatId, userName);
        }
        case USD -> {
            usdCommand(chatId);
        }
        case KZT -> {
            kztCommand(chatId);
        }
        case HELP -> {
            helpCommand(chatId);
        }
        default -> unknownCommand(chatId);
    }
    }

    private void startCommand(Long chatId, String userName){
        var text = """
                Hello, welcome to the bot, %s!
                Here you can know about the "VALUTE course" for today, linking to the CenterBank
                
                For using the bot , write commands:
                /usd - for dollar
                /kzt - for tenge
                
                optional command:
                /help - for help
                """;
        var formattedText = String.format(text, userName);
        sendMessage(chatId,formattedText);
    }

    private void usdCommand(Long chatId){
        String formattedText;
        try {
            var usd = exchangeRateService.getUSDExchangeRate();
            var text = "Dollar for %s will be in %s rub ";
            formattedText = String.format(text, LocalDate.now(), usd);
        }catch (ServiceException e){
            LOG.error("Error in getting info about dollar", e);
            formattedText = "Failed to get current data about dollar. Try later";
        }
        sendMessage(chatId,formattedText);

    }
    private void kztCommand(Long chatId){
        String formattedText;
        try {
            var usd = exchangeRateService.getKZTExchangeRate();
            var text = "Tenge for %s will be in %s rub ";
            formattedText = String.format(text, LocalDate.now(), usd);
        }catch (ServiceException e){
            LOG.error("Error in getting info about tenge", e);
            formattedText = "Failed to get current data about tenge. Try later";
        }
        sendMessage(chatId,formattedText);

    }
    private void helpCommand(Long chatId){
        var text = """
                How to use the bot? Follow instructions below:
                write this command to know about dollar in rub in current time => /usd 
                write this command to know about tenge in rub in current time => /kzt 
                
                """;
        sendMessage(chatId, text);
    }
    private void unknownCommand(Long chatId){
        var text = "No found command";
        sendMessage(chatId, text);
    }
    private void sendMessage(Long chatId, String text){
        var chatIdStr = String.valueOf(chatId);
        var sendMessage = new SendMessage(chatIdStr, text);
        try {
            execute(sendMessage);
        }catch (TelegramApiException e){
            LOG.error("Error in sending message",e);
        }
    }
}
