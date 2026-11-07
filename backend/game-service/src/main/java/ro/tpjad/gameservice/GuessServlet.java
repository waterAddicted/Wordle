package ro.tpjad.gameservice;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONArray;
import org.json.JSONObject;
import ro.tpjad.gameservice.model.GameSession;

import java.io.BufferedReader;
import java.io.IOException;

@WebServlet("/api/guess")
public class GuessServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        JSONObject body = readJson(req);

        String sessionId = body.optString("sessionId", null);
        String input = body.optString("input", "").trim().toUpperCase();

        if (sessionId == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "sessionId lipseste");
            return;
        }

        GameSession session = SessionStore.getInstance().get(sessionId);
        if (session == null) {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Sesiune necunoscuta");
            return;
        }

        if (session.getStatus() != GameSession.GameStatus.IN_PROGRESS) {
            resp.sendError(HttpServletResponse.SC_CONFLICT, "Jocul s-a incheiat deja");
            return;
        }

        if (input.length() != session.getWord().length() || !input.chars().allMatch(Character::isLetter)) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST,
                    "Cuvantul trebuie sa aiba " + session.getWord().length() + " litere");
            return;
        }

        GuessEvaluator.LetterResult[] results = GuessEvaluator.evaluate(session.getWord(), input);
        session.incrementAttempts();

        boolean won = input.equals(session.getWord());
        if (won) {
            session.setStatus(GameSession.GameStatus.WON);
        } else if (session.getAttemptsRemaining() <= 0) {
            session.setStatus(GameSession.GameStatus.LOST);
        }

        JSONArray inputLetters = new JSONArray();
        JSONArray response = new JSONArray();
        for (int i = 0; i < input.length(); i++) {
            inputLetters.put(String.valueOf(input.charAt(i)));
            response.put(results[i].name().toLowerCase());
        }

        JSONObject json = new JSONObject();
        json.put("input", inputLetters);
        json.put("response", response);
        json.put("attemptsRemaining", session.getAttemptsRemaining());
        json.put("status", session.getStatus().name());
        if (session.getStatus() != GameSession.GameStatus.IN_PROGRESS) {
            json.put("word", session.getWord());
        }

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json.toString());
    }

    private JSONObject readJson(HttpServletRequest req) throws IOException {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = req.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return new JSONObject(sb.toString());
    }
}
