package network.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

public class HttpUtil {
    public static void responseWriter(HttpExchange exchange, String response, int rCode, int respLen)
            throws IOException {
        exchange.sendResponseHeaders(rCode, respLen);
        try (OutputStream os = exchange.getResponseBody()) {
            os.write(response.getBytes());
        }
    }

    public static void responseCloser(HttpExchange exchange, int rCode, int respLen) throws IOException {
        exchange.sendResponseHeaders(rCode, respLen);
        exchange.close();
    }

    public static JsonObject getJsonObjFromRequest(HttpExchange exchange) throws IOException {
        InputStream inputStream = exchange.getRequestBody();
        String stringForJson = new String(inputStream.readAllBytes(), StandardCharsets.UTF_8);
        JsonElement jsonElement = JsonParser.parseString(stringForJson);
        if (!jsonElement.isJsonObject()) {
            throw new RuntimeException("Не верный формат для JSON");
        }
        return jsonElement.getAsJsonObject();
    }
}

