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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import nl.webutils.simplerouter.MatchedValues;
import nl.webutils.simplerouter.MethodAction;
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

        void execute(HttpServletRequest request, HttpServletResponse response, MatchedValues matchedValues) throws ServletException, IOException;
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

        public static Handler info = (request, response, matched) -> {
            String name = request.getParameter("name");
            assertEquals("Klaas", name);

            response.setContentType("text/plain");
            response.setStatus(200);
            ServletOutputStream sos = response.getOutputStream();
            sos.print("Hello World, " + name);
        };

        public static Handler payment = (request, response, matched) -> {
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
        public static Handler vardata = (request, response, matched) -> {
            String intValue = matched.getParameter("var1");
            String stringValue = matched.getParameter("var2");

            assertEquals("12345", intValue);
            assertEquals("abcde", stringValue);
        };

    }

    public RouterTest() {
    }

    @Test
    public void testCorrectRoutes() throws ServletException, IOException {
        WebRouting<Handler> router = new WebRouting.Builder<>()
                .addRoute(MethodAction.GET, "/info", RouteController.info)
                .addRoute(MethodAction.GET, "/var/(var1:int)/next/(var2:string)", RouteController.vardata)
                .addRoute(MethodAction.POST, "/transfer", RouteController.payment)
                .build();

        testInfo(router);
        testVar(router);
        testTransfer(router);
    }

    @Test
    public void testUnknownRoutes() {
        WebRouting<Handler> router = new WebRouting.Builder<>()
                .addRoute(MethodAction.GET, "/info", RouteController.info)
                .addRoute(MethodAction.POST, "/transfer", RouteController.payment)
                .build();

        // would normally result in a 404
        MatchedValues matched = router.matchEntry(MethodAction.POST, "/info");
        assertFalse(matched.isMatch());
        matched = router.matchEntry(MethodAction.GET, "/transfer");
        assertFalse(matched.isMatch());
        matched = router.matchEntry(MethodAction.GET, "/about");
        assertFalse(matched.isMatch());
    }

    @Test
    public void testConcurrency() throws InterruptedException {
        ExecutorService executor = Executors.newFixedThreadPool(5);

        WebRouting<Handler> router = new WebRouting.Builder<>()
                .addRoute(MethodAction.GET, "/info", RouteController.info)
                .addRoute(MethodAction.GET, "/var/(var1:int)/next/(var2:string)", RouteController.vardata)
                .addRoute(MethodAction.POST, "/transfer", RouteController.payment)
                .build();

        for (int i = 0; i < 5; i++) {
            final int no = i;
            executor.execute(() -> {
                for (int j = 0; j < 100; j++) {
                    testInfo(router);
                    testVar(router);
                    testTransfer(router);
                }

                System.out.println(no + ". " + Thread.currentThread().getName() + " : done executing");
            });
        }

        executor.awaitTermination(5, TimeUnit.SECONDS);

    }

    private void testInfo(WebRouting<Handler> router) {
        String pathInfo = "/info";
        String method = "GET";
        TestServletRequest request = new TestServletRequest(pathInfo, method);
        TestServletResponse response = new TestServletResponse();

        request.addParameter("name", "Klaas");

        MatchedValues matched = router.matchEntry(MethodAction.valueOf(method), pathInfo);
        assertTrue(matched.isMatch());
        Handler handler = matched.getTarget();
        try {
            handler.execute(request, response, matched);
            String responseString = new String(response.getData());
            assertEquals("Hello World, Klaas", responseString);

        } catch (Exception ex) {
            ex.printStackTrace();
            fail(ex.getMessage());
        }

    }

    private void testVar(WebRouting<Handler> router) {
        try {
            TestServletRequest request = new TestServletRequest("/var/12345/next/abcde", "GET");
            TestServletResponse response = new TestServletResponse();
            MatchedValues matched = router.matchEntry(MethodAction.valueOf("GET"), "/var/12345/next/abcde");
            assertTrue(matched.isMatch());
            Handler handler = matched.getTarget();
            handler.execute(request, response, matched);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }

    }

    private void testTransfer(WebRouting<Handler> router) {
        TestServletRequest request = new TestServletRequest("/transfer", "POST");
        TestServletResponse response = new TestServletResponse();
        MatchedValues matched  = router.matchEntry(MethodAction.valueOf("POST"), "/transfer");
        assertTrue(matched.isMatch());
        request = new TestServletRequest("/transfer", "POST");
        String json = "{"
                + "\"from\":\"Jan\","
                + "\"to\":\"Kees\","
                + "\"amount\": 1000"
                + "}";
        request.setInputStream(new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8)));
        response = new TestServletResponse();
        Handler handler = matched.getTarget();
        try {
            handler.execute(request, response, matched);
        } catch (Exception ex) {
            fail(ex.getMessage());
        }

    }
}
