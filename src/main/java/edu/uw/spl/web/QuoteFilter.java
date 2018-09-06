package edu.uw.spl.web;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.quote.AlphaVantageQuote;


public class QuoteFilter implements Filter {
    
    private static final Logger log = LoggerFactory.getLogger(QuoteFilter.class);
    private FilterConfig filterConfig;
    private ServletContext context;
    
    /*The time/date response was received*/
    private LocalDateTime timeStamp = null;
    
    /*will hold the raw bytes as a String from the respective template files- 
     * populated on init()
     */
    /**Raw bytes from the json file, as a String*/
    private String jsonFile;
    /**Raw bytes from the xml file, as a String*/
    private String xmlFile;
    /**Raw bytes from the html file, as a String*/
    private String htmlFile;
    
    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        this.filterConfig = filterConfig;
        this.context = filterConfig.getServletContext();

        /*From the context, get the appropriate init parameter value based on the form 
         * option selected (json,xml,or html)*/
        this.xmlFile = buildTemplateString(this.filterConfig.getInitParameter("xml"));
        this.htmlFile = buildTemplateString(this.filterConfig.getInitParameter("html"));
        this.jsonFile = buildTemplateString(this.filterConfig.getInitParameter("json"));
    }
    
    @Override
    public void destroy() {
        // TODO Auto-generated method stub
        /*Probably won't do anything*/
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {
        
        /*The html form parameters...*
        /*Get the format requested by the form and use it to populate the templateText string*/
        String formatReq = request.getParameter("returnType");
        /*Should target the stock symbol entered in the input*/
        String ticker = request.getParameter("symbol");
        
        String formattedTimeStamp;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy hh:mm a");
        
        /*Instantiate a custom wrapper wrapping the response*/
        ServletResponseWrapper wrapper = new ServletResponseWrapper((HttpServletResponse)response);
        
        /*Create a character stream from the wrapper output*/
        PrintWriter responseOutputStream = response.getWriter();
        
        /*The quote*/
        AlphaVantageQuote quote = AlphaVantageQuote.getQuote(ticker);
        String quoteSymbol = quote.getSymbol().toUpperCase();
        String quotePrice = Integer.toString(quote.getPrice());
        
        this.timeStamp = LocalDateTime.now();
        formattedTimeStamp = this.timeStamp.format(formatter);
        
        /*Will be the formatted xml/json/html markup with quote included*/
        String templateText;
        
        /*If response is something other than HttpServletResponse, ignore this 
         * filtering operation...*/
        if (! (response instanceof HttpServletResponse)) {
            chain.doFilter(request, response);
            return;
        }
        
        /*...Otherwise set content type based on option selected...*/
        switch (formatReq) {
            case "json":
                response.setContentType("application/json");
                templateText = String.format(this.jsonFile,
                                            quoteSymbol,
                                            quotePrice,
                                            formattedTimeStamp);
                break;
            case "html":
                response.setContentType("text/html");
                templateText = String.format(this.htmlFile,
                                                quoteSymbol,
                                                quotePrice,
                                                formattedTimeStamp);
                break;
            default:
                response.setContentType("application/xml");
                templateText = String.format(this.xmlFile,
                                                quoteSymbol,
                                                quotePrice,
                                                formattedTimeStamp);
        }
        
        /*...Then write formatted markup to the request's output stream and pass to the chain*/
        response.setContentLength(templateText.length());
        responseOutputStream.write(templateText);
        chain.doFilter(request, wrapper);
        responseOutputStream.close();
    }

    /**Utility method for populating String representations of separate file markup-
     * Obtains a file input/data input stream and reads bytes from the markup files
     * depending on the given parameter
     * @param parameter the given init parameter (xml, html, or json)
     * @return a String representation of the raw bytes from the file matching the
     * value of the given init parameter
     */
    private String buildTemplateString(final String parameter) {
        /*Get path formatted to os using init parameter value*/
        String path = parameter;
        String realPath = this.context.getRealPath(path);
        String template = null;
        
        /*Create a file with the real path*/
        File file = new File(realPath);
        
        /*Populate the template strings from the separate files*/
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            byte[] bytes = new byte[(int)file.length()];
            dis.readFully(bytes);
            template = new String(bytes, "UTF8");
        } catch (IOException e) {
            this.context.log("Error reading file " + path, e);
        }
        return template;
    }
    
}
