package app;

import java.io.DataInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.StringWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import edu.uw.ext.quote.AlphaVantageQuote;

public class QuoteApp {
    /*hard coded to test connection*/
    private static String symbol = "AAPL";
    private static final Logger log = LoggerFactory.getLogger(QuoteApp.class);
    private static String baseUrl = "http://localhost:8080/StockQuote/QuoteServlet";
    private static String getFile(String path) {
        String template = null; 
        File file = new File(path);
        
        try (DataInputStream dis = new DataInputStream(new FileInputStream(file))) {
            byte[] bytes = new byte[(int)file.length()];
            dis.readFully(bytes);
            template = new String(bytes, "UTF8");
            log.info("Successfully read file {}",file);
        } catch (IOException e) {
            log.warn("Error reading file " + file, e);
        }
        return template;
    }
    
    private static String getRequest(String urlPath,String returnType, String symbol) {
        HttpURLConnection conn = null;
        StringWriter stringWriter = new StringWriter();
        String contentType = null;
        
        switch (returnType) {
            case "html":
                contentType = "text/html";
                break;
            case "json":
                contentType = "application/json";
                break;
            case "xml":
                contentType = "application/xml";
                break;
            default:
                contentType = "plain/text";
                break;
        }
        
        try {
            /*Format to prepare a GET Request similar to index.html interaction*/
            String urlStr = String.format("%s?symbol=%s&returnType=%s&Start=Submit+Query",baseUrl,symbol,returnType);
            
            URL url = new URL(urlStr);
            conn = (HttpURLConnection)url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            //conn.setRequestProperty("charset", "UTF-8");
            //conn.setRequestProperty("Content-Type", contentType);
            
            try (InputStream in = conn.getInputStream();){
                Reader rdr = new InputStreamReader(in);
                char buf[] = new char[1024];
                int len = conn.getContentLength();
                while ((len = rdr.read(buf)) != -1) {
                    stringWriter.write(new String(buf, 0, len));
                }
                rdr.close();
            }
            
        } catch (MalformedURLException e) {
            log.warn("Given url was invalid",e);
        } catch (IOException e) {
            log.warn("Unable to connect to given url",e);
        }
        log.info("Made {} Request...",conn.getRequestMethod());
        System.out.println(stringWriter.toString());
        return stringWriter.toString();
        
    }
    
    
    public static void main(String[] args) {
        String xmlFile = getFile("src/main/webapp/templates/" + "quote.xml");
        String jsonFile = getFile("src/main/webapp/templates/" + "quote.json");
        String htmlFile = getFile("src/main/webapp/templates/" + "quote.html");
        String xmlOutput = null;
        String jsonOutput = null;
        String htmlOutput = null;
        LocalDateTime timeStamp= LocalDateTime.now();
        String formattedTimeStamp;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("MMM d yyyy hh:mm a");
        
        
        try {
            /*Get the quote then build the format*/
            AlphaVantageQuote quote = AlphaVantageQuote.getQuote(symbol);
            
            String quoteSymbol = quote.getSymbol().toUpperCase();
            String quotePrice = Integer.toString(quote.getPrice());
            timeStamp = LocalDateTime.now();
            formattedTimeStamp = timeStamp.format(formatter);
            
            htmlOutput = String.format(htmlFile,quoteSymbol,quotePrice,formattedTimeStamp);
            xmlOutput = String.format(xmlFile,quoteSymbol,quotePrice,formattedTimeStamp);
            jsonOutput = String.format(jsonFile,quoteSymbol,quotePrice,formattedTimeStamp);
            
            /*Make a GET request with the requested format*/
            getRequest(baseUrl,"xml",quoteSymbol);
            getRequest(baseUrl,"html",quoteSymbol);
            getRequest(baseUrl,"json",quoteSymbol);
            
            /*Format response from a single GET request (which seems to work much better)*/
            System.out.println("*****HTML MARKUP*****");
            System.out.println(htmlOutput);
            System.out.println("*****XML MARKUP*****");
            System.out.println(xmlOutput);
            System.out.println("*****JSON MARKUP*****");
            System.out.println(jsonOutput);
            
        } catch (IOException e) {
            log.warn("Error loading template files",e);
        }
        
    }
}
