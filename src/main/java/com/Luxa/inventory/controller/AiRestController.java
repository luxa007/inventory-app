package com.Luxa.inventory.controller;

import com.Luxa.inventory.controller.MultipartInputStreamFileResource;
import org.springframework.http.*;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.*;

@RestController
@RequestMapping("/api")
public class AiRestController {

    // URL of your Python FastAPI sidecar
    private static final String AI_SERVICE_URL = "http://localhost:8000/predict";

    private final RestTemplate restTemplate = new RestTemplate();

    /**
     * POST /api/scan
     * Accepts a single product image, forwards to the Python AI service,
     * and returns { product_name, category }.
     */
    @PostMapping("/scan")
    public ResponseEntity<Map<String, Object>> scan(
            @RequestParam("image") MultipartFile image) {

        return forwardToAiService(image);
    }

    /**
     * POST /api/bulk-scan
     * Accepts multiple images, scans them in parallel,
     * and returns a list of { product_name, category } objects.
     */
    @PostMapping("/bulk-scan")
    public ResponseEntity<List<Map<String, Object>>> bulkScan(
            @RequestParam("images") List<MultipartFile> images) {

        // Use a thread pool to scan all images concurrently
        ExecutorService executor = Executors.newFixedThreadPool(
                Math.min(images.size(), 5)); // max 5 parallel requests

        List<Future<Map<String, Object>>> futures = new ArrayList<>();

        for (MultipartFile image : images) {
            futures.add(executor.submit(() -> {
                ResponseEntity<Map<String, Object>> response = forwardToAiService(image);
                if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
                    return response.getBody();
                }
                // Return an error placeholder on failure
                Map<String, Object> error = new HashMap<>();
                error.put("product_name", "");
                error.put("category", "");
                error.put("error", "Scan failed");
                return error;
            }));
        }

        executor.shutdown();

        List<Map<String, Object>> results = new ArrayList<>();
        for (Future<Map<String, Object>> future : futures) {
            try {
                results.add(future.get(30, TimeUnit.SECONDS));
            } catch (Exception e) {
                Map<String, Object> error = new HashMap<>();
                error.put("product_name", "");
                error.put("category", "");
                error.put("error", "Timeout or error");
                results.add(error);
            }
        }

        return ResponseEntity.ok(results);
    }

    // ----------------------------------------------------------------
    // Shared helper: forward one image to the Python FastAPI service
    // ----------------------------------------------------------------
    @SuppressWarnings("unchecked")
    private ResponseEntity<Map<String, Object>> forwardToAiService(MultipartFile image) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.MULTIPART_FORM_DATA);

            MultiValueMap<String, Object> body = new LinkedMultiValueMap<>();
            body.add("image", new MultipartInputStreamFileResource(
                    image.getInputStream(), image.getOriginalFilename()));

            HttpEntity<MultiValueMap<String, Object>> requestEntity =
                    new HttpEntity<>(body, headers);

            ResponseEntity<Map> response = restTemplate.exchange(
                    AI_SERVICE_URL,
                    HttpMethod.POST,
                    requestEntity,
                    Map.class);

            return ResponseEntity.ok((Map<String, Object>) response.getBody());

        } catch (Exception e) {
            Map<String, Object> error = new HashMap<>();
            error.put("error", "AI service unavailable: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.SERVICE_UNAVAILABLE).body(error);
        }
    }
}
