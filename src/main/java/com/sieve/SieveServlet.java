package com.sieve;

import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

@WebServlet("/primes")
public class SieveServlet extends HttpServlet {

    private static final int MAX_LIMIT = 15000;

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws IOException {

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");

        String param = req.getParameter("limit");

        // Validate input exists
        if (param == null || param.isBlank()) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\": \"Missing 'limit' parameter.\"}");
            return;
        }

        int limit;
        try {
            limit = Integer.parseInt(param.trim());
        } catch (NumberFormatException e) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\": \"'limit' must be an integer.\"}");
            return;
        }

        // Validate range
        if (limit < 2) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\": \"'limit' must be at least 2.\"}");
            return;
        }

        if (limit > MAX_LIMIT) {
            resp.setStatus(400);
            resp.getWriter().write("{\"error\": \"You can only request primes up to 15,000.\"}");
            return;
        }

        // Sieve of Eratosthenes
        boolean[] isComposite = new boolean[limit + 1];
        for (int i = 2; (long) i * i <= limit; i++) {
            if (!isComposite[i]) {
                for (int j = i * i; j <= limit; j += i) {
                    isComposite[j] = true;
                }
            }
        }

        List<Integer> primes = new ArrayList<>();
        for (int i = 2; i <= limit; i++) {
            if (!isComposite[i]) primes.add(i);
        }

        // Build JSON response manually (no external deps)
        StringBuilder sb = new StringBuilder();
        sb.append("{\"limit\":").append(limit);
        sb.append(",\"count\":").append(primes.size());
        sb.append(",\"primes\":[");
        for (int i = 0; i < primes.size(); i++) {
            if (i > 0) sb.append(",");
            sb.append(primes.get(i));
        }
        sb.append("]}");

        resp.setStatus(200);
        resp.getWriter().write(sb.toString());
    }
}
