package eu.tw;

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

@ApplicationScoped
public class LogWriter {

    private final ConcurrentLinkedQueue<LogEntry> queue = new ConcurrentLinkedQueue<>();
    private final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSXXX");

    public LogWriter() {
        String path = Path.of(fileName()).toAbsolutePath().toString();
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
                .toList();
        if (lines.isEmpty()) {
            return;
        }
        try {
            Files.write(Path.of(fileName()), lines, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.APPEND);
            System.out.println("Wrote " + lines.size() + " log entries.");
        } catch (IOException e) {
            System.out.println("Failed to write logfile: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String toEntry(LogEntry logEntry) {
        return String.format("[%d][%s][%s][%s][%s] %s", logEntry.date.getTime(), sdf.format(logEntry.date), logEntry.channel, logEntry.sender, logEntry.character, logEntry.message);
    }

    private String fileName() {
        LocalDateTime now = LocalDateTime.now();
        return "./" + now.getYear() + "_" + now.getMonthValue() + "_" + now.getDayOfMonth() + "_rpr_chat.log";
    }

}
