package ro.tpjad.gameservice;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.json.JSONObject;
import ro.tpjad.gameservice.model.GameSession;

import java.io.IOException;
import java.util.UUID;

@WebServlet("/api/game")
public class GameServlet extends HttpServlet {

    private final WordsDbClient wordsDbClient = new WordsDbClient();

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        String word;
        try {
            word = wordsDbClient.fetchWord();
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "Intrerupt la apelarea WordsDatabase");
            return;
        } catch (IOException e) {
            resp.sendError(HttpServletResponse.SC_SERVICE_UNAVAILABLE, "WordsDatabase indisponibil");
            return;
        }

        String sessionId = UUID.randomUUID().toString();
        GameSession session = new GameSession(sessionId, word);
        SessionStore.getInstance().save(session);

        JSONObject json = new JSONObject();
        json.put("sessionId", sessionId);
        json.put("wordLength", word.length());
        json.put("maxAttempts", GameSession.MAX_ATTEMPTS);
        json.put("attemptsRemaining", session.getAttemptsRemaining());

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        resp.getWriter().write(json.toString());
    }
}
