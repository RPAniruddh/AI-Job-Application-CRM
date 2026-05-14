package com.jobcrm.ai.dto;

import java.util.List;

public class EmbeddingResponse {

    private List<EmbeddingData> data;

    public List<EmbeddingData> getData() { return data; }
    public void setData(List<EmbeddingData> data) { this.data = data; }

    public static class EmbeddingData {
        private List<Float> embedding;

        public List<Float> getEmbedding() { return embedding; }
        public void setEmbedding(List<Float> embedding) { this.embedding = embedding; }
    }
}