package eu.tw;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class Config {
    private final Set<String> relevantCharacters;
    public Config() {
        Path configPath = Paths.get("./config.txt");
        if (Files.exists(configPath)) {
            try {
                String value = Files.readString(configPath, StandardCharsets.UTF_8);
                this.relevantCharacters = Stream.of(value.split("\n"))
                        .map(String::trim)
                        .filter(line -> line.startsWith("RELEVANT_CHARACTERS"))
                        .findFirst()
                        .map(Config::getRelevantCharacters)
                        .orElse(Set.of());
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.relevantCharacters = Set.of();
        }
        System.out.println("The following characters are relevant:");
        this.relevantCharacters.forEach(System.out::println);
    }

    static Set<String> getRelevantCharacters(String line) {
        String[] parts = line.split("=");
        return Stream.of(parts[1].split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    public boolean isRelevant(String character) {
        return relevantCharacters.contains(character.trim().toLowerCase());
    }
}
