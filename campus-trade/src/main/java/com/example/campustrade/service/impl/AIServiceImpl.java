package com.example.campustrade.service.impl;

import com.example.campustrade.common.BusinessException;
import com.example.campustrade.component.AdminChecker;
import com.example.campustrade.dto.ProductAIDTO;
import com.example.campustrade.mapper.AIReviewLogMapper;
import com.example.campustrade.service.AIService;
import com.example.campustrade.vo.ProductAIOptimizeVO;
import com.example.campustrade.vo.ProductAIReviewVO;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
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

    private final AIReviewLogMapper aiReviewLogMapper;

    private final AdminChecker adminChecker;

    public AIServiceImpl(ObjectMapper objectMapper, AIReviewLogMapper aiReviewLogMapper, AdminChecker adminChecker) {
        this.objectMapper = objectMapper;
        this.aiReviewLogMapper = aiReviewLogMapper;
        this.adminChecker = adminChecker;
    }

    @Override
    public ProductAIOptimizeVO optimizeProduct(ProductAIDTO dto) {

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

    @Override
    public ProductAIReviewVO reviewProduct(ProductAIDTO dto) {
        ProductAIReviewVO vo;
        try {
            String prompt = "你是校园二手交易平台的商品审核助手。"
                    + "请判断用户发布的商品标题、描述和价格是否适合在校园二手交易平台发布。"
                    + "审核规则："
                    + "1. 如果商品涉及违法、违规、色情、暴力、赌博、诈骗、违禁品、危险品、虚假交易等内容，返回 REJECT。"
                    + "2. 如果标题或描述明显是广告、引流、辱骂、无意义乱码，也返回 REJECT。"
                    + "3. 如果内容是正常二手商品交易，返回 PASS。"
                    + "4. 不要因为描述简单就拒绝，只在明显不适合发布时拒绝。"
                    + "返回要求：只返回 JSON 字符串，不要返回任何解释，不要使用 Markdown 代码块。"
                    + "JSON 格式固定为：{\"suggestion\":\"PASS或REJECT\",\"reason\":\"简短中文原因\"}。"
                    + "其中 suggestion 只能是 PASS 或 REJECT。"
                    + "商品标题：" + dto.getTitle()
                    + "商品描述：" + dto.getDescription()
                    + "商品价格：" + dto.getPrice();

            Map<String,Object> message = new HashMap<>();
            message.put("role","user");
            message.put("content",prompt);
            Map<String,Object> requestBody = new HashMap<>();
            requestBody.put("model","deepseek-v4-flash");
            requestBody.put("messages",List.of(message));
            requestBody.put("temperature",0.3);
            String requestJson = objectMapper.writeValueAsString(requestBody);

            HttpClient client = HttpClient.newHttpClient();

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(baseUrl))
                    .header("Content-Type","application/json")
                    .header("Authorization","Bearer "+apiKey)
                    .POST(HttpRequest.BodyPublishers.ofString(requestJson))
                    .build();

            HttpResponse<String> response = client.send(request,HttpResponse.BodyHandlers.ofString());

            if(response.statusCode() != 200){
                throw new BusinessException("AI请求失败");
            }

            JsonNode root = objectMapper.readTree(response.body());

            String content = root.path("choices")
                    .get(0)
                    .path("message")
                    .path("content")
                    .asText();

            vo = objectMapper.readValue(content,ProductAIReviewVO.class);

        }catch (Exception e){
            throw new BusinessException("AI服务请求失败");
        }
        return vo;
    }


}
