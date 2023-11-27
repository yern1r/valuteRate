package com.jai_bot_by_tutorial_bot.exchange_rates_bot.sevice;

import com.jai_bot_by_tutorial_bot.exchange_rates_bot.exception.ServiceException;


public interface ExchangeRateService {

    String getUSDExchangeRate() throws ServiceException;

    String getKZTExchangeRate() throws ServiceException;
}
