package com.jobcrm.ai.client;

import com.jobcrm.ai.dto.ChatRequest;
import com.jobcrm.ai.dto.ChatResponse;
import com.jobcrm.ai.dto.EmbeddingRequest;
import com.jobcrm.ai.dto.EmbeddingResponse;
import com.jobcrm.config.AppProperties;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;

@Component
public class OpenAiClient {

    private final WebClient webClient;

    private final String apiKey;

    public OpenAiClient(AppProperties appProperties) {
        this.apiKey = appProperties.getOpenAi().getApiKey();

        this.webClient = WebClient.builder()
                .baseUrl(appProperties.getOpenAi().getBaseUrl())
                .defaultHeader("Content-Type", "application/json")
                .build();
    }

    public EmbeddingResponse generateEmbedding(EmbeddingRequest request) {
        return webClient.post()
                .uri("/embeddings")
                .headers(headers -> headers.setBearerAuth(apiKey)) 
                .bodyValue(request)
                .retrieve()
                .bodyToMono(EmbeddingResponse.class)
                .block();
    }

    public ChatResponse chat(ChatRequest request) {
        return webClient.post()
                .uri("/chat/completions")
                .headers(headers -> headers.setBearerAuth(apiKey)) 
                .bodyValue(request)
                .retrieve()
                .bodyToMono(ChatResponse.class)
                .block();
    }
}