package pl.pozdro320.utils;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class EnhancedRequest {
    private String url;

    public static Builder builder() { return new Builder(); }

    public static class Builder {
        private final EnhancedRequest request = new EnhancedRequest();
        public Builder url(String url) { request.url = url; return this; }
        public EnhancedRequest build() { return request; }
    }

    public EnhancedResponse send() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) new URL(url).openConnection();
        conn.setRequestProperty("User-Agent", "Mozilla/5.0");
        InputStreamReader reader = new InputStreamReader(conn.getInputStream());
        return new EnhancedResponse(JsonParser.parseReader(reader).getAsJsonObject());
    }

    public record EnhancedResponse(JsonObject getAsJson) {}
}