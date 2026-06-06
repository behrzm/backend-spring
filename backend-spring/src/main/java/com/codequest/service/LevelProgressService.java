package com.codequest.service;

import com.codequest.domain.entity.LevelProgress;
import com.codequest.dto.LevelProgressDto;
import com.codequest.dto.UpdateLevelProgressRequest;
import com.codequest.repository.LevelProgressRepository;
import com.codequest.security.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional
public class LevelProgressService {

    private final LevelProgressRepository repository;

    public LevelProgressService(LevelProgressRepository repository) {
        this.repository = repository;
    }

    public Optional<LevelProgressDto> getLevelProgress(String language, String track, Integer levelId) {
        String userId = SecurityContext.getUserId();
        return repository.findByUserIdAndLanguageAndTrackAndLevelId(userId, language, track, levelId)
                .map(LevelProgressDto::from);
    }

    public List<LevelProgressDto> getAllProgress() {
        String userId = SecurityContext.getUserId();
        return repository.findByUserId(userId).stream()
                .map(LevelProgressDto::from)
                .collect(Collectors.toList());
    }

    public LevelProgressDto updateLevelProgress(UpdateLevelProgressRequest request) {
        String userId = SecurityContext.getUserId();
        LevelProgress progress = repository.findByUserIdAndLanguageAndTrackAndLevelId(
                userId, request.getLanguage(), request.getTrack(), request.getLevelId())
                .orElseGet(() -> new LevelProgress(userId, request.getLanguage(), request.getTrack(), request.getLevelId()));

        if (request.getStars() != null) {
            progress.setStars(Math.max(progress.getStars() == null ? 0 : progress.getStars(), request.getStars()));
        }
        if (request.getCompleted() != null && request.getCompleted()) {
            progress.setCompleted(true);
        }
        if (request.getSolutionCode() != null) {
            progress.setSavedCode(request.getSolutionCode());
        }

        return LevelProgressDto.from(repository.save(progress));
    }
}
