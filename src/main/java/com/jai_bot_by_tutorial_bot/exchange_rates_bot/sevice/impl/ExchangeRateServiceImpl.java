package com.jai_bot_by_tutorial_bot.exchange_rates_bot.sevice.impl;

import com.jai_bot_by_tutorial_bot.exchange_rates_bot.client.CbrClient;
import com.jai_bot_by_tutorial_bot.exchange_rates_bot.exception.ServiceException;
import com.jai_bot_by_tutorial_bot.exchange_rates_bot.sevice.ExchangeRateService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;


import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.StringReader;

@Service
public class ExchangeRateServiceImpl implements ExchangeRateService {
    private final static String USD_XPATH = "/ValCurs//Valute[@ID='R01235']/VunitRate";
    private final static String KZT_XPATH = "/ValCurs//Valute[@ID='R01335']/VunitRate";


    @Autowired
    private CbrClient client;

    @Override
    public String getUSDExchangeRate() throws ServiceException {
        var xml = client.getCurrencyRateXML();
        return extractCurrencyValueFromXML(xml,USD_XPATH);
    }

    @Override
    public String getKZTExchangeRate() throws ServiceException {
        var xml = client.getCurrencyRateXML();
        return extractCurrencyValueFromXML(xml,KZT_XPATH);
    }

    private static String extractCurrencyValueFromXML(String xml, String xpathExpression) throws ServiceException{
    var source = new InputSource(new StringReader(xml));
    try {
        var xpath = XPathFactory.newInstance().newXPath();
        var document = (Document) xpath.evaluate("/", source, XPathConstants.NODE);

        return xpath.evaluate(xpathExpression, document);
    }catch (XPathExpressionException e){
        throw new ServiceException("Error in the parsing", e);
    }
    }
}
