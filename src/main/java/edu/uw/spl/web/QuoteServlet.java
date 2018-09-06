package edu.uw.spl.web;

import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.FilterConfig;
import javax.servlet.ServletConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.quote.AlphaVantageQuote;

public class QuoteServlet extends HttpServlet {

    private static final Logger log = LoggerFactory.getLogger(QuoteServlet.class);
    private static final long serialVersionUID = 1L;
    private AlphaVantageQuote quote;
    private ServletConfig context;
    
    /*The time/date response was received*/
    private LocalDateTime timeStamp = null;
    
    /**Default constructor*/
    public QuoteServlet() {}
    
    @Override
    public void init(ServletConfig servletConfig) {
        this.context = servletConfig;
    }
    
    /**
     * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doGet(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        
        serviceRequest(request,response);
    }

    @Override
    /**
     * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
     */
    protected void doPost(HttpServletRequest request, HttpServletResponse response) 
            throws ServletException, IOException {
        serviceRequest(request,response);
        
    }
    
    private void serviceRequest(HttpServletRequest request, HttpServletResponse response) {
        String ticker = request.getParameter("symbol");
        String reply = null;
        String formattedTimeStamp = null;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy hh:mm a");
        
        /*Default markup (xml)*/
        String replyXml = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\r\n" + 
                "<stock-quote><stock-ticker>%s</stock-ticker>" + 
                "<stock-price>%s</stock-price></stock-ticker>" +
                "<response-timedate>%s</response-timedate>" +
                "</stock-quote>";
        
        
        try {
            quote = AlphaVantageQuote.getQuote(ticker);
            this.timeStamp = LocalDateTime.now();//%$3tc
            formattedTimeStamp = this.timeStamp.format(formatter);
            
            reply = String.format(replyXml,
                                    quote.getSymbol(),
                                    quote.getPrice(),
                                    formattedTimeStamp); 
            
            response.setContentType("text/xml");
            response.setContentLength(reply.length());
            PrintWriter writer = response.getWriter();
            writer.print(reply);
            
        } catch (IOException e) {
            log.warn("Unable to get quote from server",e);
        }
    }

}
