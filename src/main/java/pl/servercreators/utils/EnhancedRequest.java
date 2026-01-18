package pl.servercreators.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import lombok.Builder;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

@Builder
public class EnhancedRequest {
    private final String url;

    public EnhancedResponse send() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        
        try (InputStreamReader reader = new InputStreamReader(conn.getInputStream())) {
            JsonParser parser = new JsonParser();
            JsonObject json = parser.parse(reader).getAsJsonObject();
            return new EnhancedResponse(json);
        }
    }

    public record EnhancedResponse(JsonObject getAsJson) {}
}