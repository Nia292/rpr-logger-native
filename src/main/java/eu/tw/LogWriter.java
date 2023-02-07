package eu.tw;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.quarkus.scheduler.Scheduled;

import javax.enterprise.context.ApplicationScoped;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentLinkedQueue;
import java.util.stream.Collectors;

@ApplicationScoped
public class LogWriter {

    private final ConcurrentLinkedQueue<LogEntry> queue = new ConcurrentLinkedQueue<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");
    private final ObjectMapper objectMapper = new ObjectMapper();

    public LogWriter() {
        String path = Path.of(fileNameText()).toAbsolutePath().normalize().toString();
        System.out.println("Logging path is: " + path);
    }

    void enQueue(LogEntry logEntry) {
        queue.offer(logEntry);
    }

    @Scheduled(every = "30s")
    void writeToFile() {
        List<LogEntry> result = new ArrayList<>();
        for (LogEntry last = queue.poll(); last != null; last = queue.poll()) {
            result.add(last);
        }
        // Queue empty => dump to file
        List<String> lines = result.stream()
                .map(this::toEntry)
                .collect(Collectors.toList());
        List<String> jsonLines = result.stream()
                .map(this::toJsonEntry)
                .collect(Collectors.toList());
        if (lines.isEmpty()) {
            return;
        }
        try {
            Files.write(Path.of(fileNameText()), lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            Files.write(Path.of(fileNameJson()), jsonLines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Wrote " + lines.size() + " log entries.");
        } catch (IOException e) {
            System.out.println("Failed to write logfile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String toEntry(LogEntry logEntry) {
        return String.format("[%s][%d][%s][%s][%s][%s] %s", logEntry.server, logEntry.date.getTime(), sdf.format(logEntry.date), logEntry.channel, logEntry.sender, logEntry.character, logEntry.message);
    }

    private String toJsonEntry(LogEntry logEntry) {
        return objectMapper
                .createObjectNode()
                .put("time", logEntry.date.getTime())
                .put("channel", logEntry.channel)
                .put("sender", logEntry.sender)
                .put("character", logEntry.character)
                .put("message", logEntry.message)
                .put("server", logEntry.server)
                .toString();
    }

    private String fileNameText() {
        LocalDateTime now = LocalDateTime.now();
        return "./" + now.getYear() + "_" + now.getMonthValue() + "_" + now.getDayOfMonth() + "_rpr_chat.log";
    }

    private String fileNameJson() {
        LocalDateTime now = LocalDateTime.now();
        return "./" + now.getYear() + "_" + now.getMonthValue() + "_" + now.getDayOfMonth() + "_rpr_chat.json";
    }
}
