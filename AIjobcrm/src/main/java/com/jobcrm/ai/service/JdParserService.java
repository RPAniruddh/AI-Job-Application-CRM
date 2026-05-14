package com.jobcrm.ai.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobcrm.ai.client.OpenAiClient;
import com.jobcrm.ai.dto.ChatRequest;
import com.jobcrm.ai.dto.ChatResponse;
import com.jobcrm.ai.dto.ParsedJobDescription;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class JdParserService {

    private final OpenAiClient openAiClient;
    private final ObjectMapper objectMapper;

    public JdParserService(OpenAiClient openAiClient, ObjectMapper objectMapper) {
        this.openAiClient = openAiClient;
        this.objectMapper = objectMapper;
    }

    public ParsedJobDescription parse(String rawJobDescription) {
        String systemPrompt = """
                You are a job description parser. Extract structured information from job descriptions.
                You must respond with ONLY valid JSON — no markdown, no code blocks, no explanation.
                Use exactly this structure:
                {
                  "company": "company name or null",
                  "roleTitle": "job title",
                  "salaryRange": "salary range or null",
                  "requiredSkills": ["skill1", "skill2"],
                  "niceToHaveSkills": ["skill1", "skill2"],
                  "summary": "2-3 sentence summary of the role"
                }
                """;

        ChatRequest request = new ChatRequest(
                "gpt-4o-mini",
                List.of(
                        new ChatRequest.Message("system", systemPrompt),
                        new ChatRequest.Message("user", rawJobDescription)
                ),
                0.1  // low temperature = more deterministic, structured output
        );

        ChatResponse response = openAiClient.chat(request);

        if (response == null || response.getFirstContent() == null) {
            throw new RuntimeException("Failed to parse job description — no response from OpenAI");
        }

        try {
            String json = response.getFirstContent().trim();
            
            // Strip all possible markdown wrapping
            if (json.startsWith("```")) {
                // Remove opening code fence (```json or ``` or ```JSON)
                json = json.replaceAll("(?i)^```[a-z]*\\n?", "");
                // Remove closing code fence
                json = json.replaceAll("```\\s*$", "");
                json = json.trim();
            }
            
            // Find the actual JSON object — start from first { to last }
            int start = json.indexOf('{');
            int end = json.lastIndexOf('}');
            if (start != -1 && end != -1 && end > start) {
                json = json.substring(start, end + 1);
            }
            
            return objectMapper.readValue(json, ParsedJobDescription.class);
        } catch (Exception e) {
            throw new RuntimeException("Failed to parse OpenAI response as JSON: " 
                + e.getMessage());
        }
    }
}