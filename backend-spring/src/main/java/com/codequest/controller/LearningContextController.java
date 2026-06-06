package com.codequest.controller;

import com.codequest.dto.LearningContextDto;
import com.codequest.dto.UpdateLearningContextRequest;
import com.codequest.service.LearningContextService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.Optional;

@RestController
@RequestMapping("/levels")
@Tag(name = "Learning Context", description = "Learning context for AI assistant")
@SecurityRequirement(name = "bearerAuth")
public class LearningContextController {

    private final LearningContextService learningContextService;

    public LearningContextController(LearningContextService learningContextService) {
        this.learningContextService = learningContextService;
    }

    @GetMapping("/learning-context")
    @Operation(summary = "Get learning context", description = "Returns learning context for AI assistant")
    public ResponseEntity<?> getLearningContext(
            @RequestParam String language,
            @RequestParam String track,
            @RequestParam Integer levelId) {
        Optional<LearningContextDto> context = learningContextService.getLearningContext(language, track, levelId);
        return ResponseEntity.ok(context.orElse(null));
    }

    @PutMapping("/learning-context")
    @Operation(summary = "Update learning context", description = "Updates context after failed attempt")
    public ResponseEntity<LearningContextDto> updateLearningContext(@RequestBody UpdateLearningContextRequest request) {
        return ResponseEntity.ok(learningContextService.updateLearningContext(request));
    }
}

