/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package nl.webutils.test.utils;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.Collection;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.ServletOutputStream;
import javax.servlet.WriteListener;
import javax.servlet.http.Cookie;

/**
 *
 * @author rudol
 */
public class TestServletResponse implements javax.servlet.http.HttpServletResponse {

    final ByteArrayOutputStream bos = new ByteArrayOutputStream();
    private int                status;
    private Map<String,String> headers;
    private String             location;
    private String             contentType       = "text/html";
    private String             characterEncoding = "UTF-8";

    public TestServletResponse() {
        headers = new HashMap<>();
    }
    
    
    
    
    ServletOutputStream sos = new ServletOutputStream() {
        
        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {
            throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
        }

        @Override
        public void write(int b) throws IOException {
            bos.write(b);
        }

        @Override
        public void write(byte[] b) throws IOException {
            bos.write(b); 
        }

        @Override
        public void write(byte[] b, int off, int len) throws IOException {
            bos.write(b, off, len); 
        }
        
        
        
    };
    
    public byte[] getData() {
        try {
            bos.flush();
        } catch (IOException ex) {
            Logger.getLogger(TestServletResponse.class.getName()).log(Level.SEVERE, null, ex);
        }
        return bos.toByteArray();
    }

    public String getLocation() {
        return location;
    }
    
    
    
    @Override
    public void addCookie(Cookie cookie) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean containsHeader(String name) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String encodeURL(String url) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String encodeRedirectURL(String url) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String encodeUrl(String url) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public String encodeRedirectUrl(String url) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void sendError(int sc, String msg) throws IOException {
        status = sc;
    }

    @Override
    public void sendError(int sc) throws IOException {
        status = sc;
    }

    @Override
    public void sendRedirect(String location) throws IOException {
        this.location = location;
    }

    @Override
    public void setDateHeader(String name, long date) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void addDateHeader(String name, long date) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public void setHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public void addHeader(String name, String value) {
        headers.put(name, value);
    }

    @Override
    public void setIntHeader(String name, int value) {
        headers.put(name, String.valueOf(value));
    }

    @Override
    public void addIntHeader(String name, int value) {
        headers.put(name, String.valueOf(value));
    }

    @Override
    public void setStatus(int sc) {
        status = sc;
    }

    @Override
    public void setStatus(int sc, String sm) {
        status = sc;
    }

    @Override
    public int getStatus() {
        return status;
    }

    @Override
    public String getHeader(String name) {
        return headers.get(name);
    }

    @Override
    public Collection<String> getHeaders(String name) {
        return headers.values();
    }

    @Override
    public Collection<String> getHeaderNames() {
        return headers.keySet();
    }

    @Override
    public String getCharacterEncoding() {
        return characterEncoding;
    }

    @Override
    public String getContentType() {
        return contentType;
    }

    @Override
    public ServletOutputStream getOutputStream() throws IOException {
        return sos;
    }

    @Override
    public PrintWriter getWriter() throws IOException {
        return new PrintWriter(sos, true);
    }

    @Override
    public void setCharacterEncoding(String charset) {
        this.characterEncoding = charset;
    }

    @Override
    public void setContentLength(int len) {

    }

    @Override
    public void setContentLengthLong(long len) {
    }

    @Override
    public void setContentType(String type) {
        this.contentType = type;
    }

    @Override
    public void setBufferSize(int size) {
        
    }

    @Override
    public int getBufferSize() {
        return 0;
    }

    @Override
    public void flushBuffer() throws IOException {
        sos.flush();
    }

    @Override
    public void resetBuffer() {
        ;
    }

    @Override
    public boolean isCommitted() {
        return false;
    }

    @Override
    public void reset() {
        ;
    }

    @Override
    public void setLocale(Locale loc) {
        ;
    }

    @Override
    public Locale getLocale() {
        return Locale.getDefault();
    }
    
}
