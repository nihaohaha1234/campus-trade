package com.example.campustrade.service.impl;

import com.example.campustrade.common.BusinessException;
import com.example.campustrade.dto.ProductAIOptimizeDTO;
import com.example.campustrade.service.AIService;
import com.example.campustrade.vo.ProductAIOptimizeVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AIServiceImpl implements AIService {

    @Value("${deepseek.base-url}")
    private String baseUrl;

    @Value("${deepseek.api-key}")
    private String apiKey;

    private final ObjectMapper objectMapper;

    public AIServiceImpl(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    @Override
    public ProductAIOptimizeVO optimizeProduct(ProductAIOptimizeDTO dto) {

        ProductAIOptimizeVO vo;
        try {
            String prompt = "请你帮我优化校园二手交易平台的标题和描述" +
                    "要求：标题简洁真实，不夸张；描述自然清楚，适合同校学生查看;" +
                    "只返回JSON格式，不要返回其他解释。" +
                    "JSON格式如下:{\"title\":\"优化后的标题\",\"description\":\"优化后的描述\"}" +
                    "原商品标题:" + dto.getTitle() +
                    "原商品描述:" + dto.getDescription() +
                    "原商品价格:" + dto.getPrice();
            Map<String, Object> message = new HashMap<>();
            message.put("role", "user");
            message.put("content", prompt);

            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put("model", "deepseek-v4-flash");
            requestBody.put("messages", List.of(message));
            requestBody.put("temperature", 0.7);
            String requestJson = objectMapper.writeValueAsString(requestBody);
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type", "application/json")
                    .header("Authorization", "Bearer " + apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();
            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            if (response.statusCode() != 200) {
                throw new BusinessException("AI服务请求失败");
            }
            JsonNode root = objectMapper.readTree(response.body());
            String content = root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();
            vo = objectMapper.readValue(content, ProductAIOptimizeVO.class);
        } catch (Exception e) {
            throw new BusinessException("AI服务请求失败");
        }
        return vo;
    }
}
