package eu.tw;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.UriInfo;
import java.util.Date;

@Path("/rpr-webhook")
public class RprResource {

    private final LogWriter logWriter;
    private final Config config;

    public RprResource(LogWriter logWriter) {
        this.logWriter = logWriter;
        this.config = new Config();
    }

    @GET
    @Produces(MediaType.APPLICATION_JSON)
    public String receiveMessage(@Context UriInfo uriInfo) {
        String message = uriInfo.getQueryParameters().getFirst("message");
        String sender = uriInfo.getQueryParameters().getFirst("sender");
        String character = uriInfo.getQueryParameters().getFirst("character");
        String radius = uriInfo.getQueryParameters().getFirst("radius");
        String location = uriInfo.getQueryParameters().getFirst("location");
        String channel = uriInfo.getQueryParameters().getFirst("channel");
        if (config.isRelevant(character)) {
            logWriter.enQueue(new LogEntry(message, sender, character, radius, location, channel, new Date()));
        } else {
            System.out.println("Skipping irrelevant message from " + character);
        }
        return "{}";
    }
}