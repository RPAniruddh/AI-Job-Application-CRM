package com.jobcrm.ai.controller;

import com.jobcrm.ai.dto.ParsedJobDescription;
import com.jobcrm.ai.service.FitScoringService;
import com.jobcrm.ai.service.JdParserService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/ai")
public class AiController {

    private final JdParserService jdParserService;
    private final FitScoringService fitScoringService;

    public AiController(JdParserService jdParserService,
                        FitScoringService fitScoringService) {
        this.jdParserService = jdParserService;
        this.fitScoringService = fitScoringService;
    }

    // Parse a raw job description into structured data
    @PostMapping("/parse-jd")
    public ResponseEntity<ParsedJobDescription> parseJobDescription(
            @RequestBody Map<String, String> body) {
        String rawJd = body.get("description");
        if (rawJd == null || rawJd.isBlank()) {
            throw new RuntimeException("description field is required");
        }
        return ResponseEntity.ok(jdParserService.parse(rawJd));
    }

    // Upload resume text and generate embedding
    @PostMapping("/resume")
    public ResponseEntity<Map<String, String>> uploadResume(
            @RequestBody Map<String, String> body) {
        String resumeText = body.get("resumeText");
        if (resumeText == null || resumeText.isBlank()) {
            throw new RuntimeException("resumeText field is required");
        }
        fitScoringService.saveResume(resumeText);
        return ResponseEntity.ok(Map.of("message", "Resume saved and embedded successfully"));
    }

    // Score a job application against the user's resume
    @PostMapping("/score/{applicationId}")
    public ResponseEntity<Map<String, Object>> scoreApplication(
            @PathVariable UUID applicationId) {
        int fitScore = fitScoringService.scoreApplication(applicationId);
        return ResponseEntity.ok(Map.of(
                "applicationId", applicationId,
                "fitScore", fitScore,
                "message", fitScore >= 70
                        ? "Strong match!"
                        : fitScore >= 50
                        ? "Moderate match"
                        : "Low match — consider tailoring your resume"
        ));
    }
}