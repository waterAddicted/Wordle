package ro.tpjad.gameservice;

import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;

// calls the WordsDatabase servlet over HTTP
public class WordsDbClient {

    private static final String WORDS_DB_URL =
            System.getenv().getOrDefault("WORDS_DB_URL", "http://wordsdb:8080/wordsdatabase") + "/api/word";

    private final HttpClient httpClient = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(5))
            .build();

    public String fetchWord() throws IOException, InterruptedException {
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(WORDS_DB_URL))
                .timeout(Duration.ofSeconds(5))
                .GET()
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());
        if (response.statusCode() != 200) {
            throw new IOException("WordsDatabase a raspuns cu status " + response.statusCode());
        }

        JSONObject json = new JSONObject(response.body());
        return json.getString("word").toUpperCase();
    }
}
