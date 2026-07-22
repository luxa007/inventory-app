package com.Luxa.inventory.service;

import com.Luxa.inventory.model.Product;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class RestockAdvisorService {

    @Value("${anthropic.api.key}")
    private String apiKey;

    private static final String CLAUDE_API_URL = "https://api.anthropic.com/v1/messages";
    private static final String MODEL = "claude-haiku-4-5-20251001";
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    public Map<String, Object> getAdvice(List<Product> products) {
        String summary = buildInventorySummary(products);
        String prompt = buildPrompt(summary);
        try {
            String raw = callClaude(prompt);
            return parseAdvice(raw, products);
        } catch (Exception e) {
            return fallbackAdvice(products, e.getMessage());
        }
    }

    private String buildInventorySummary(List<Product> products) {
        long lowStock = products.stream().filter(Product::isLowStock).count();
        long outOfStock = products.stream().filter(p -> p.getQuantity() == 0).count();
        String criticalItems = products.stream()
                .filter(Product::isLowStock)
                .sorted(Comparator.comparingInt(Product::getQuantity))
                .limit(10)
                .map(p -> String.format("- %s (qty: %d, threshold: %d, velocity: %.1f/day, price: $%s)",
                        p.getName(), p.getQuantity(), p.getMinThreshold(),
                        p.getSalesVelocity(), p.getPrice()))
                .collect(Collectors.joining("\n"));
        return String.format(
                "Total products: %d | Low stock: %d | Out of stock: %d\n\nCritical items:\n%s",
                products.size(), lowStock, outOfStock,
                criticalItems.isEmpty() ? "None" : criticalItems);
    }

    private String buildPrompt(String summary) {
        return "You are an inventory management AI for SmartStock, a retail inventory system.\n"
                + "Analyse this inventory snapshot and provide actionable restock advice.\n\n"
                + "INVENTORY SNAPSHOT:\n" + summary + "\n\n"
                + "Respond in this exact JSON format (no markdown, no extra text):\n"
                + "{\n"
                + "  \"overall_status\": \"critical|warning|healthy\",\n"
                + "  \"summary\": \"One sentence summary of inventory health\",\n"
                + "  \"immediate_actions\": [\"action1\", \"action2\", \"action3\"],\n"
                + "  \"restock_recommendations\": [\n"
                + "    {\"product\": \"name\", \"current_qty\": 0, \"recommended_order\": 0, \"reason\": \"brief reason\", \"priority\": \"high|medium|low\"}\n"
                + "  ],\n"
                + "  \"insights\": \"One paragraph of strategic inventory insights\"\n"
                + "}";
    }

    @SuppressWarnings("unchecked")
    private String callClaude(String prompt) {
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set("x-api-key", apiKey);
        headers.set("anthropic-version", "2023-06-01");

        Map<String, Object> message = new HashMap<>();
        message.put("role", "user");
        message.put("content", prompt);

        Map<String, Object> body = new HashMap<>();
        body.put("model", MODEL);
        body.put("max_tokens", 1024);
        body.put("messages", List.of(message));

        HttpEntity<Map<String, Object>> request = new HttpEntity<>(body, headers);
        ResponseEntity<Map> response = restTemplate.exchange(
                CLAUDE_API_URL, HttpMethod.POST, request, Map.class);

        List<Map<String, Object>> content =
                (List<Map<String, Object>>) response.getBody().get("content");
        return (String) content.get(0).get("text");
    }

    @SuppressWarnings("unchecked")
    private Map<String, Object> parseAdvice(String raw, List<Product> products) {
        try {
            String clean = raw.replaceAll("```json|```", "").trim();
            Map<String, Object> parsed = objectMapper.readValue(clean, Map.class);
            parsed.put("success", true);
            parsed.put("total_products", products.size());
            parsed.put("low_stock_count", products.stream().filter(Product::isLowStock).count());
            return parsed;
        } catch (Exception e) {
            Map<String, Object> result = new LinkedHashMap<>();
            result.put("success", true);
            result.put("summary", raw);
            result.put("overall_status", "unknown");
            result.put("total_products", products.size());
            result.put("low_stock_count", products.stream().filter(Product::isLowStock).count());
            return result;
        }
    }

    private Map<String, Object> fallbackAdvice(List<Product> products, String error) {
        long lowStock = products.stream().filter(Product::isLowStock).count();
        Map<String, Object> result = new LinkedHashMap<>();
        result.put("success", false);
        result.put("overall_status", lowStock > 0 ? "warning" : "healthy");
        result.put("summary", "AI advisor temporarily unavailable. Basic analysis: "
                + lowStock + " products need restocking.");
        result.put("error", error);
        result.put("total_products", products.size());
        result.put("low_stock_count", lowStock);
        return result;
    }

    public String getAdvice(Product p) {
        if (p.getQuantity() <= p.getMinThreshold()) {
            return "Critical: Restock immediately. Sales velocity is " + p.getSalesVelocity();
        }
        return "Stock levels are currently stable.";
    }
}
