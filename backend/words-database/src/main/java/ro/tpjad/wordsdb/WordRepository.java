package ro.tpjad.wordsdb;

import java.io.IOException;
import java.io.InputStream;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;

// singleton, loads words.txt once
public class WordRepository {

    private static final String SHARED_VOLUME_PATH = "/data/words.txt";

    private static final WordRepository INSTANCE = new WordRepository();

    private final List<String> words;

    private WordRepository() {
        this.words = Collections.unmodifiableList(loadWords());
    }

    public static WordRepository getInstance() {
        return INSTANCE;
    }

    public String randomWord() {
        int index = ThreadLocalRandom.current().nextInt(words.size());
        return words.get(index);
    }

    public int size() {
        return words.size();
    }

    private List<String> loadWords() {
        List<String> raw;
        Path sharedPath = Path.of(SHARED_VOLUME_PATH);
        if (Files.isReadable(sharedPath)) {
            raw = readLines(() -> Files.newInputStream(sharedPath));
        } else {
            raw = readLines(() -> getClass().getResourceAsStream("/words.txt"));
        }

        List<String> cleaned = new ArrayList<>();
        for (String line : raw) {
            String word = line.trim().toUpperCase();
            if (word.length() == 5 && word.chars().allMatch(Character::isLetter)) {
                cleaned.add(word);
            }
        }
        if (cleaned.isEmpty()) {
            throw new IllegalStateException("Nu s-a putut incarca lista de cuvinte");
        }
        Collections.sort(cleaned);
        return cleaned;
    }

    private interface StreamSupplier {
        InputStream get() throws IOException;
    }

    private List<String> readLines(StreamSupplier supplier) {
        List<String> lines = new ArrayList<>();
        try (InputStream in = supplier.get()) {
            if (in == null) {
                throw new IllegalStateException("Fisierul words.txt nu a fost gasit");
            }
            BufferedReader reader = new BufferedReader(new InputStreamReader(in, StandardCharsets.UTF_8));
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.isBlank()) {
                    lines.add(line);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Eroare la citirea words.txt", e);
        }
        return lines;
    }
}
