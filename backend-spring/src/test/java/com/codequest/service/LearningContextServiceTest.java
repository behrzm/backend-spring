package com.codequest.service;

import com.codequest.domain.entity.UserLearningProfile;
import com.codequest.dto.UpdateLearningContextRequest;
import com.codequest.repository.UserLearningProfileRepository;
import com.codequest.security.SecurityContext;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class LearningContextServiceTest {

    @Mock
    private UserLearningProfileRepository userLearningProfileRepository;

    private LearningContextService learningContextService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        learningContextService = new LearningContextService(userLearningProfileRepository);
    }

    @Test
    void testUpdateLearningContextCreatesNewIfNotExists() {
        String userId = "test-user";
        SecurityContext.setUserId(userId);

        when(userLearningProfileRepository.findByUserIdAndLanguageAndTrackAndLevelId(userId, "python", "beginner", 1))
                .thenReturn(Optional.empty());
        when(userLearningProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var request = new UpdateLearningContextRequest("python", "beginner", 1, "add(a b)", "missing colon");
        var result = learningContextService.updateLearningContext(request);

        assertNotNull(result);
        assertEquals(1, result.getAttempts());
        assertEquals("add(a b)", result.getFailedCommands());
        assertEquals("missing colon", result.getLastError());
        verify(userLearningProfileRepository).save(any());
    }

    @Test
    void testUpdateLearningContextIncrementsAttempts() {
        String userId = "test-user";
        SecurityContext.setUserId(userId);

        UserLearningProfile existingContext = new UserLearningProfile(userId, "python", "beginner", 1);
        existingContext.setAttempts(2);

        when(userLearningProfileRepository.findByUserIdAndLanguageAndTrackAndLevelId(userId, "python", "beginner", 1))
                .thenReturn(Optional.of(existingContext));
        when(userLearningProfileRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));

        var request = new UpdateLearningContextRequest("python", "beginner", 1, "new error", "syntax error");
        var result = learningContextService.updateLearningContext(request);

        assertEquals(3, result.getAttempts());
    }
}

