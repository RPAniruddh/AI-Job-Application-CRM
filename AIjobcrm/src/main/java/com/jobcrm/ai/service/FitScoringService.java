package com.jobcrm.ai.service;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.jobcrm.ai.model.UserResume;
import com.jobcrm.ai.repository.UserResumeRepository;
import com.jobcrm.application.model.JobApplication;
import com.jobcrm.application.repository.JobApplicationRepository;
import com.jobcrm.auth.model.User;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FitScoringService {

    private final UserResumeRepository userResumeRepository;
    private final JobApplicationRepository applicationRepository;
    private final EmbeddingService embeddingService;
    private final ObjectMapper objectMapper;

    public FitScoringService(UserResumeRepository userResumeRepository,
                             JobApplicationRepository applicationRepository,
                             EmbeddingService embeddingService,
                             ObjectMapper objectMapper) {
        this.userResumeRepository = userResumeRepository;
        this.applicationRepository = applicationRepository;
        this.embeddingService = embeddingService;
        this.objectMapper = objectMapper;
    }

    private User getCurrentUser() {
        return (User) SecurityContextHolder.getContext()
                .getAuthentication().getPrincipal();
    }

    // ── Resume Upload ─────────────────────────────────────────────

    public void saveResume(String resumeText) {
        User user = getCurrentUser();

        List<Float> embedding = embeddingService.generateEmbedding(resumeText);
        String embeddingJson = serializeEmbedding(embedding);

        UserResume resume = userResumeRepository
                .findByUserId(user.getId())
                .orElse(new UserResume());

        resume.setUser(user);
        resume.setResumeText(resumeText);
        resume.setEmbeddingJson(embeddingJson);

        userResumeRepository.save(resume);
    }

    // ── Fit Scoring ───────────────────────────────────────────────

    public int scoreApplication(java.util.UUID applicationId) {
        User user = getCurrentUser();

        // Get the job application
        JobApplication application = applicationRepository
                .findByIdAndUserId(applicationId, user.getId())
                .orElseThrow(() -> new RuntimeException("Application not found"));

        if (application.getRawDescription() == null
                || application.getRawDescription().isBlank()) {
            throw new RuntimeException(
                    "Application has no job description — add one before scoring");
        }

        // Get user's resume embedding
        UserResume resume = userResumeRepository
                .findByUserId(user.getId())
                .orElseThrow(() -> new RuntimeException(
                        "No resume found — upload your resume first"));

        // Generate JD embedding
        List<Float> jdEmbedding = embeddingService
                .generateEmbedding(application.getRawDescription());

        // Get resume embedding
        List<Float> resumeEmbedding = deserializeEmbedding(resume.getEmbeddingJson());

        // Calculate cosine similarity
        double similarity = cosineSimilarity(resumeEmbedding, jdEmbedding);

        // Convert to 0-100 score
        int fitScore = (int) Math.round(similarity * 100);

        // Save score to application
        application.setFitScore(fitScore);
        applicationRepository.save(application);

        return fitScore;
    }

    // ── Cosine Similarity Math ────────────────────────────────────

    private double cosineSimilarity(List<Float> vectorA, List<Float> vectorB) {
        if (vectorA.size() != vectorB.size()) {
            throw new RuntimeException("Vector dimensions do not match");
        }

        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;

        for (int i = 0; i < vectorA.size(); i++) {
            dotProduct += vectorA.get(i) * vectorB.get(i);
            normA += Math.pow(vectorA.get(i), 2);
            normB += Math.pow(vectorB.get(i), 2);
        }

        if (normA == 0 || normB == 0) return 0.0;

        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB));
    }

    // ── Serialization Helpers ─────────────────────────────────────

    private String serializeEmbedding(List<Float> embedding) {
        try {
            return objectMapper.writeValueAsString(embedding);
        } catch (Exception e) {
            throw new RuntimeException("Failed to serialize embedding: " + e.getMessage());
        }
    }

    private List<Float> deserializeEmbedding(String json) {
        try {
            return objectMapper.readValue(json, new TypeReference<List<Float>>() {});
        } catch (Exception e) {
            throw new RuntimeException("Failed to deserialize embedding: " + e.getMessage());
        }
    }
}