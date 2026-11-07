package ro.tpjad.gameservice.model;

public class GameSession {

    public static final int MAX_ATTEMPTS = 6;

    private final String sessionId;
    private final String word;
    private int attemptsUsed;
    private GameStatus status;

    public GameSession(String sessionId, String word) {
        this.sessionId = sessionId;
        this.word = word;
        this.attemptsUsed = 0;
        this.status = GameStatus.IN_PROGRESS;
    }

    public String getSessionId() {
        return sessionId;
    }

    public String getWord() {
        return word;
    }

    public int getAttemptsUsed() {
        return attemptsUsed;
    }

    public void incrementAttempts() {
        attemptsUsed++;
    }

    public int getAttemptsRemaining() {
        return MAX_ATTEMPTS - attemptsUsed;
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public enum GameStatus {
        IN_PROGRESS, WON, LOST
    }
}
