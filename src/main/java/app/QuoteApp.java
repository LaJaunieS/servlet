package app;

import java.io.IOException;

import edu.uw.ext.quote.AlphaVantageQuote;

public class QuoteApp {
    /*hard coded to test connection*/
    static String symbol = "AMZN"; 
    
    public static void main(String[] args) {
        try {
            AlphaVantageQuote quote = AlphaVantageQuote.getQuote(symbol);
            System.out.format("%s price at %s",quote.getSymbol(),quote.getPrice());
        } catch (IOException e) {
            e.printStackTrace();
        }
        
    }
}
