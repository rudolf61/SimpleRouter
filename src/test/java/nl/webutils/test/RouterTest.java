package nl.webutils.test;

/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.nio.charset.StandardCharsets;
import java.util.Optional;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.webutils.simplerouter.MethodAction;
import nl.webutils.simplerouter.RouteEntry;
import nl.webutils.simplerouter.WebRouting;
import nl.webutils.test.utils.TestServletRequest;
import nl.webutils.test.utils.TestServletResponse;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author rudol
 */
public class RouterTest {
    
    private static final ObjectMapper mapper = new ObjectMapper();
    
    public interface Handler {
        void execute(HttpServletRequest request, HttpServletResponse response, RouteEntry routeEntry) throws ServletException , IOException;
    }

    public static class Transfer {
        private String from;
        private String to;
        private int amount;

        public String getFrom() {
            return from;
        }

        public void setFrom(String from) {
            this.from = from;
        }

        public String getTo() {
            return to;
        }

        public void setTo(String to) {
            this.to = to;
        }

        public int getAmount() {
            return amount;
        }

        public void setAmount(int amount) {
            this.amount = amount;
        }
        
        
    }
    
    public static class RouteController {

        public static Handler info = (request, response, route) -> { 
            String name = request.getParameter("name");
            assertEquals("Klaas", name);

            response.setContentType("text/plain");
            response.setStatus(200);
            ServletOutputStream sos = response.getOutputStream();
            sos.print("Hello World, " + name);
        };
        
        public static Handler payment = (request, response, route) -> { 
            InputStream is = request.getInputStream();
            Transfer transfer = mapper.readValue(is, Transfer.class);
            assertEquals("Jan", transfer.getFrom());
            assertEquals("Kees", transfer.getTo());
            assertEquals(1000, transfer.getAmount());
            response.setContentType("text/plain");
            response.setStatus(200);
            PrintWriter writer = response.getWriter();
            writer.write("Transfer processed");
        };
        private static Handler  vardata = (request, response, route) -> {
            String intValue    = route.getParameter("var1");
            String stringValue = route.getParameter("var2");
            
            assertEquals("12345", intValue);
            assertEquals("abcde", stringValue);
        };

    }
    
    public RouterTest() {
    }
    

    @Test
    public void testCorrectRoutes() throws ServletException, IOException {
        WebRouting<Handler> router = new WebRouting
                .Builder<>()
                .addRoute(MethodAction.GET, "/info", RouteController.info)
                .addRoute(MethodAction.GET, "/var/(var1:int)/next/(var2:string)", RouteController.vardata)
                .addRoute(MethodAction.POST, "/transfer", RouteController.payment)
                .build();
        
        String pathInfo = "/info";
        String method   = "GET";
        TestServletRequest  request  = new TestServletRequest(pathInfo, method);
        TestServletResponse response = new TestServletResponse();
        
        request.addParameter("name", "Klaas");

        Optional<RouteEntry<Handler>> entryOptional = router.matchEntry(MethodAction.valueOf(method), pathInfo);
        assertTrue(entryOptional.isPresent());
        RouteEntry<Handler> entry = entryOptional.get();
        Handler handler = entry.getTarget();
        handler.execute(request, response, entry);
        String responseString = new String(response.getData());
        assertEquals("Hello World, Klaas", responseString);

        
        entryOptional = router.matchEntry(MethodAction.valueOf(method), "/var/12345/next/abcde");
        assertTrue(entryOptional.isPresent());
        entry = entryOptional.get();
        handler = entry.getTarget();
        handler.execute(request, response, entry);

        entryOptional = router.matchEntry(MethodAction.valueOf("POST"), "/transfer");
        assertTrue(entryOptional.isPresent());
        request  = new TestServletRequest("/transfer", "POST");
        String json = "{"
                + "\"from\":\"Jan\","
                + "\"to\":\"Kees\","
                + "\"amount\": 1000"
                + "}";
        request.setInputStream(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        response = new TestServletResponse();
        entry = entryOptional.get();
        handler = entry.getTarget();
        handler.execute(request, response, entry);
        
        
        
    }

    @Test
    public void testUnknownRoutes() {
        WebRouting<Handler> router = new WebRouting
                .Builder<>()
                .addRoute(MethodAction.GET, "/info", RouteController.info)
                .addRoute(MethodAction.POST, "/transfer", RouteController.payment)
                .build();

        // would normally result in a 404
        Optional<RouteEntry<Handler>> optionalEntry = router.matchEntry(MethodAction.POST, "/info");
        assertFalse(optionalEntry.isPresent());
        optionalEntry = router.matchEntry(MethodAction.GET, "/transfer");
        assertFalse(optionalEntry.isPresent());
        optionalEntry = router.matchEntry(MethodAction.GET, "/about");
        assertFalse(optionalEntry.isPresent());
    }

    
}
