package eu.tw;

import java.util.Date;

public class LogEntry {
    public final String message;
    public final String sender;
    public final String character;
    public final String radius;
    public final String location;
    public final String channel;
    public final Date date;
    public final String server;

    public LogEntry(String message,
                    String sender,
                    String character,
                    String radius,
                    String location,
                    String channel,
                    Date date,
                    String server) {
        this.message = message;
        this.sender = sender;
        this.character = character;
        this.radius = radius;
        this.location = location;
        this.channel = channel;
        this.date = date;
        this.server = server;
    }

}
