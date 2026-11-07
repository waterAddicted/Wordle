package ro.tpjad.gameservice;

import ro.tpjad.gameservice.model.GameSession;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

// singleton, in-memory session store
public class SessionStore {

    private static final SessionStore INSTANCE = new SessionStore();

    private final Map<String, GameSession> sessions = new ConcurrentHashMap<>();

    private SessionStore() {
    }

    public static SessionStore getInstance() {
        return INSTANCE;
    }

    public void save(GameSession session) {
        sessions.put(session.getSessionId(), session);
    }

    public GameSession get(String sessionId) {
        return sessions.get(sessionId);
    }
}
