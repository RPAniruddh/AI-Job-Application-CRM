package com.jobcrm.ai.service;

import com.jobcrm.ai.client.OpenAiClient;
import com.jobcrm.ai.dto.EmbeddingRequest;
import com.jobcrm.ai.dto.EmbeddingResponse;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class EmbeddingService {

    private final OpenAiClient openAiClient;

    public EmbeddingService(OpenAiClient openAiClient) {
        this.openAiClient = openAiClient;
    }

    public List<Float> generateEmbedding(String text) {
        EmbeddingRequest request = new EmbeddingRequest(
                "text-embedding-ada-002",
                text
        );

        EmbeddingResponse response = openAiClient.generateEmbedding(request);

        if (response == null
                || response.getData() == null
                || response.getData().isEmpty()) {
            throw new RuntimeException("Failed to generate embedding from OpenAI");
        }

        return response.getData().get(0).getEmbedding();
    }

    // Converts List<Float> to float[] for pgvector storage
    public float[] toFloatArray(List<Float> embedding) {
        float[] result = new float[embedding.size()];
        for (int i = 0; i < embedding.size(); i++) {
            result[i] = embedding.get(i);
        }
        return result;
    }

    // Converts float[] back to List<Float> for cosine similarity math
    public List<Float> toFloatList(float[] embedding) {
        List<Float> result = new java.util.ArrayList<>();
        for (float f : embedding) {
            result.add(f);
        }
        return result;
    }
}