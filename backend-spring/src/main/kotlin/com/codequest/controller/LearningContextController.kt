package com.codequest.controller

import com.codequest.dto.LearningContextDto
import com.codequest.dto.UpdateLearningContextRequest
import com.codequest.service.LearningContextService
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*

@RestController
@RequestMapping("/levels")
@Tag(name = "Learning Context", description = "Learning context for AI assistant")
@SecurityRequirement(name = "bearerAuth")
class LearningContextController(
    private val learningContextService: LearningContextService
) {

    @GetMapping("/learning-context")
    @Operation(summary = "Get learning context", description = "Returns learning context for AI assistant")
    fun getLearningContext(
        @RequestParam language: String,
        @RequestParam track: String,
        @RequestParam levelId: Int
    ): ResponseEntity<LearningContextDto?> {
        return ResponseEntity.ok(learningContextService.getLearningContext(language, track, levelId))
    }

    @PutMapping("/learning-context")
    @Operation(summary = "Update learning context", description = "Updates context after failed attempt")
    fun updateLearningContext(@RequestBody request: UpdateLearningContextRequest): ResponseEntity<LearningContextDto> {
        return ResponseEntity.ok(learningContextService.updateLearningContext(request))
    }
}

