package eu.tw;

import java.util.Date;

public record LogEntry(String message,
                       String sender,
                       String character,
                       String radius,
                       String location,
                       String channel,
                       Date date) {
}
