package com.codequest.service;

import com.codequest.domain.entity.UserLearningProfile;
import com.codequest.dto.LearningContextDto;
import com.codequest.dto.UpdateLearningContextRequest;
import com.codequest.repository.UserLearningProfileRepository;
import com.codequest.security.SecurityContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.OffsetDateTime;
import java.util.Optional;

@Service
@Transactional
public class LearningContextService {

    private static final Logger logger = LoggerFactory.getLogger(LearningContextService.class);
    private final UserLearningProfileRepository userLearningProfileRepository;

    public LearningContextService(UserLearningProfileRepository userLearningProfileRepository) {
        this.userLearningProfileRepository = userLearningProfileRepository;
    }

    public Optional<LearningContextDto> getLearningContext(String language, String track, Integer levelId) {
        String userId = SecurityContext.getUserId();
        if (userId == null) {
            throw new IllegalStateException("User not authenticated");
        }

        Optional<UserLearningProfile> context = userLearningProfileRepository
                .findByUserIdAndLanguageAndTrackAndLevelId(userId, language, track, levelId);

        return context.map(LearningContextDto::from);
    }

    public LearningContextDto updateLearningContext(UpdateLearningContextRequest request) {
        String userId = SecurityContext.getUserId();
        if (userId == null) {
            throw new IllegalStateException("User not authenticated");
        }

        Optional<UserLearningProfile> existingContext = userLearningProfileRepository
                .findByUserIdAndLanguageAndTrackAndLevelId(
                        userId, request.getLanguage(), request.getTrack(), request.getLevelId()
                );

        UserLearningProfile context;
        if (existingContext.isPresent()) {
            context = existingContext.get();
        } else {
            context = new UserLearningProfile(userId, request.getLanguage(), 
                                             request.getTrack(), request.getLevelId());
        }

        context.setAttempts(context.getAttempts() + 1);
        if (request.getFailedCommands() != null) {
            context.setFailedCommands(request.getFailedCommands());
        }
        if (request.getLastError() != null) {
            context.setLastError(request.getLastError());
        }
        context.setUpdatedAt(OffsetDateTime.now());

        userLearningProfileRepository.save(context);
        logger.info("Updated learning context for user {}: {} {} level {}, attempts: {}", 
                   userId, request.getLanguage(), request.getTrack(), 
                   request.getLevelId(), context.getAttempts());

        return LearningContextDto.from(context);
    }
}

