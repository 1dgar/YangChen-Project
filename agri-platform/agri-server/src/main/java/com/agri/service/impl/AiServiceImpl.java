package com.agri.service.impl;

import com.agri.service.AiService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.*;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Service
public class AiServiceImpl implements AiService {

    @Value("${ai.zhipu.api-key:45cdbd7a78f545c4a0658681bde56a93.3s7KONYjqIUMcNsR}")
    private String apiKey;

    @Value("${ai.zhipu.model:glm-4-flash}")
    private String model;

    private static final String ZHIPU_API_URL = "https://open.bigmodel.cn/api/paas/v4/chat/completions";
    
    private final RestTemplate restTemplate = new RestTemplate();
    private final ObjectMapper objectMapper = new ObjectMapper();

    private static final String SYSTEM_PROMPT = 
        "你是农产品交易平台的智能助手，名叫「小农助手」。你是一个专业、友好、热情的农业顾问。\n\n" +
        "【你的专业领域】\n" +
        "1. 农产品种植技术：包括蔬菜、水果、粮油作物等的种植方法、病虫害防治、施肥浇水等\n" +
        "2. 农产品市场行情：价格走势分析、供需信息、销售渠道等\n" +
        "3. 农产品保鲜储存：各类农产品的储存方法、保鲜技巧、运输注意事项等\n" +
        "4. 平台使用指导：如何发布产品、发布供需信息、注册登录等平台功能\n" +
        "5. 农业政策解读：惠农政策、补贴政策、认证标准等\n\n" +
        "【回答要求】\n" +
        "1. 回答要专业准确，有理有据\n" +
        "2. 语言简洁明了，避免过于冗长\n" +
        "3. 适当使用列表、分点说明，让回答更有条理\n" +
        "4. 对于种植技术问题，给出具体的操作步骤和注意事项\n" +
        "5. 对于市场行情问题，提供实用的建议和分析\n" +
        "6. 如果问题与农业无关，礼貌地引导用户回到农业相关话题\n\n" +
        "【回答格式】\n" +
        "- 使用**加粗**强调重点内容\n" +
        "- 使用数字列表(1. 2. 3.)或符号列表(•)组织内容\n" +
        "- 适当分段，让回答更易阅读\n\n" +
        "请用亲切友好的语气与用户交流，让用户感受到你的专业和热情。";

    @Override
    public String chat(String message, String history) {
        try {
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            headers.set("Authorization", "Bearer " + apiKey);

            ObjectNode requestBody = objectMapper.createObjectNode();
            requestBody.put("model", model);
            requestBody.put("stream", false);
            
            ArrayNode messages = requestBody.putArray("messages");
            
            ObjectNode systemMessage = messages.addObject();
            systemMessage.put("role", "system");
            systemMessage.put("content", SYSTEM_PROMPT);
            
            if (history != null && !history.isEmpty()) {
                try {
                    JsonNode historyArray = objectMapper.readTree(history);
                    if (historyArray.isArray()) {
                        for (JsonNode msg : historyArray) {
                            ObjectNode historyMsg = messages.addObject();
                            historyMsg.put("role", msg.get("role").asText());
                            historyMsg.put("content", msg.get("content").asText());
                        }
                    }
                } catch (Exception e) {
                    log.warn("解析历史消息失败: {}", e.getMessage());
                }
            }
            
            ObjectNode userMessage = messages.addObject();
            userMessage.put("role", "user");
            userMessage.put("content", message);

            HttpEntity<String> request = new HttpEntity<>(objectMapper.writeValueAsString(requestBody), headers);
            
            ResponseEntity<String> response = restTemplate.exchange(
                ZHIPU_API_URL,
                HttpMethod.POST,
                request,
                String.class
            );

            if (response.getStatusCode() == HttpStatus.OK && response.getBody() != null) {
                JsonNode responseJson = objectMapper.readTree(response.getBody());
                JsonNode choices = responseJson.get("choices");
                if (choices != null && choices.isArray() && choices.size() > 0) {
                    JsonNode content = choices.get(0).get("message").get("content");
                    if (content != null) {
                        return content.asText();
                    }
                }
            }
            
            return "抱歉，我暂时无法回答您的问题，请稍后再试。";
        } catch (Exception e) {
            log.error("调用智谱AI失败: {}", e.getMessage(), e);
            return "抱歉，AI服务暂时不可用，请稍后再试。";
        }
    }

    @Override
    public String chatWithContext(String message, String context) {
        String enhancedMessage = context != null && !context.isEmpty() 
            ? "当前页面上下文：" + context + "\n\n用户问题：" + message 
            : message;
        return chat(enhancedMessage, null);
    }
}
