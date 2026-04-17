package com.restaurant.KDS.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

@Service
public class AiService {

    @Value("${deepseek.api.key}")
    private String apiKey;
    private static final String URL = "https://api.deepseek.com/v1/chat/completions";

    private final HttpClient httpClient = HttpClient.newHttpClient();
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Builds a JSON to send to DeepSeek API, then receives the response and coverts it.
     *
     * @param prompt the exact prompt to ask the LLM
     * @return the LLM response
     */
    public String askAi(String prompt) throws Exception {
        // Build the message
        ObjectNode message = mapper.createObjectNode();
        message.put("role", "user");
        message.put("content", prompt);

        ArrayNode messages = mapper.createArrayNode().add(message);

        ObjectNode body = mapper.createObjectNode();
        body.put("model", "deepseek-chat");
        body.set("messages", messages);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(URL))
                .header("Content-Type", "application/json")
                .header("Authorization", "Bearer " + apiKey)
                .POST(HttpRequest.BodyPublishers.ofString(mapper.writeValueAsString(body)))
                .build();

        HttpResponse<String> response = httpClient.send(request, HttpResponse.BodyHandlers.ofString());

        JsonNode responseJson = mapper.readTree(response.body());
        return responseJson.at("/choices/0/message/content").asText();
    }
}