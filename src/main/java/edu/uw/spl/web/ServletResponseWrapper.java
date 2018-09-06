package edu.uw.spl.web;

import java.io.CharArrayWriter;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;

/*Wraps response and utilizes a char array writer that acts as a character buffer for 
 * writing to the response output stream*/
public class ServletResponseWrapper extends HttpServletResponseWrapper {

    private CharArrayWriter output; 
    
    public ServletResponseWrapper(HttpServletResponse response) {
        super(response);
        this.output = new CharArrayWriter();
        // TODO Auto-generated constructor stub
    }
    
    @Override
    public String toString() {
        return output.toString();
    }

}
