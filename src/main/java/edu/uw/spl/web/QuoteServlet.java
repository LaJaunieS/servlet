package edu.uw.spl.web;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import edu.uw.ext.quote.AlphaVantageQuote;

public class QuoteServlet extends HttpServlet {

    private static final long serialVersionUID = 1L;
    private AlphaVantageQuote quote;
    
    public QuoteServlet() {
        
    }
    
    @Override
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String replyHtml = "<h1>%s : %s</h1>";
        quote = AlphaVantageQuote.getQuote("AMZN");
        String reply = String.format(replyHtml,quote.getSymbol(),quote.getPrice()); 
        
        response.setContentType("text/html");
        response.setContentLength(reply.length());
        PrintWriter writer = response.getWriter();
        writer.print(reply);
        
    }

    @Override
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        String replyHtml = "<h1>%s : %s</h1>";
        quote = AlphaVantageQuote.getQuote("AMZN");
        String reply = String.format(replyHtml,quote.getSymbol(),quote.getPrice()); 
        
        response.setContentType("text/html");
        response.setContentLength(reply.length());
        PrintWriter writer = response.getWriter();
        writer.print(reply);
        
    }

}
