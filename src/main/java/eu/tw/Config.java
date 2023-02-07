package eu.tw;

import io.quarkus.runtime.StartupEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.enterprise.context.ApplicationScoped;
import javax.enterprise.event.Observes;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApplicationScoped
public class Config {
    private final Set<String> relevantCharacters;
    private final boolean allRelevant;
    private final String secret;

    public Config() {
        Path configPath = Paths.get("./config.txt");
        if (Files.exists(configPath)) {
            try {
                String configFileValue = Files.readString(configPath, StandardCharsets.UTF_8);
                this.relevantCharacters = getConfigValue(configFileValue, "RELEVANT_CHARACTERS")
                        .map(Config::getRelevantCharacters)
                        .orElse(Set.of());
                this.allRelevant = getConfigValue(configFileValue,"ALL_RELEVANT").isPresent();
                this.secret = getConfigValue(configFileValue, "SECRET").orElse("changeme");
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        } else {
            this.relevantCharacters = Set.of();
            this.allRelevant = true;
            this.secret = "local-instance";
        }
    }

    static Set<String> getRelevantCharacters(String line) {
        return Stream.of(line.split(","))
                .map(String::trim)
                .map(String::toLowerCase)
                .collect(Collectors.toSet());
    }

    public boolean isRelevant(String character) {
        if (allRelevant) {
            return true;
        }
        return relevantCharacters.contains(character.trim().toLowerCase());
    }

    public boolean secretMatches(String secret) {
        return this.secret.matches(secret);
    }

    private Optional<String> getConfigValue(String configFileContent, String configKey) {
        return Stream.of(configFileContent.split("\n"))
                .map(String::trim)
                .filter(line -> line.startsWith(configKey))
                .findFirst()
                .map(s -> s.split("=")[1].trim());
    }

    void startup(@Observes StartupEvent event) {
        System.out.println("####################");
        System.out.println("#### RPR Logger ####");
        System.out.println("####################");
        System.out.println("- Secret: " + this.secret);
        if (this.secret.equals("local-instance")) {
            System.out.println("- Webhook URL: http://localhost:15194/rpr-logger/local/local-instance");
        } else {
            System.out.println("- Webhook URL: http://localhost:15194/rpr-logger/<server>/" + this.secret);
        }
    }
}
