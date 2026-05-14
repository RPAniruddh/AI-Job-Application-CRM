package com.jobcrm.ai.dto;

import java.util.List;

public class EmbeddingRequest {

    private String model;
    private List<String> input;

    public EmbeddingRequest(String model, String input) {
        this.model = model;
        this.input = List.of(input);
    }

    public String getModel() { return model; }
    public List<String> getInput() { return input; }
}