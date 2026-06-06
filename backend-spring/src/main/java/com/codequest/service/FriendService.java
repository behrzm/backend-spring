package com.codequest.service;

import com.codequest.domain.entity.Friendship;
import com.codequest.domain.entity.Profile;
import com.codequest.dto.FriendDto;
import com.codequest.dto.ProfileDto;
import com.codequest.repository.FriendshipRepository;
import com.codequest.repository.ProfileRepository;
import com.codequest.security.SecurityContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class FriendService {

    private final FriendshipRepository friendshipRepository;
    private final ProfileRepository profileRepository;

    public FriendService(FriendshipRepository friendshipRepository, ProfileRepository profileRepository) {
        this.friendshipRepository = friendshipRepository;
        this.profileRepository = profileRepository;
    }

    public List<FriendDto> getFriends() {
        String userId = SecurityContext.getUserId();
        List<Friendship> friendships = friendshipRepository.findAllAcceptedFriends(userId);
        
        return friendships.stream().map(f -> {
            String friendId = f.getUserId().equals(userId) ? f.getFriendId() : f.getUserId();
            Profile p = profileRepository.findById(friendId).orElseThrow();
            return new FriendDto(
                p.getId(), 
                p.getDisplayName(), 
                p.getXp(), 
                p.getLevel(), 
                p.getElo(),
                p.getStreak(),
                p.getWins(),
                p.getAvatarUrl(), 
                "ACCEPTED"
            );
        }).collect(Collectors.toList());
    }

    public List<FriendDto> getPendingRequests() {
        String userId = SecurityContext.getUserId();
        return friendshipRepository.findByFriendIdAndStatus(userId, "PENDING").stream()
                .map(f -> {
                    Profile p = profileRepository.findById(f.getUserId()).orElseThrow();
                    return new FriendDto(p.getId(), p.getDisplayName(), p.getXp(), p.getLevel(), p.getElo(), p.getStreak(), p.getWins(), p.getAvatarUrl(), "PENDING");
                })
                .collect(Collectors.toList());
    }

    public void handleRequest(String senderId, String action) {
        String userId = SecurityContext.getUserId();
        Friendship f = friendshipRepository.findByUserIdAndFriendId(senderId, userId)
                .orElseThrow(() -> new RuntimeException("Request not found"));
        
        if ("ACCEPT".equalsIgnoreCase(action)) {
            f.setStatus("ACCEPTED");
            friendshipRepository.save(f);
        } else {
            friendshipRepository.delete(f);
        }
    }

    public List<ProfileDto> searchPlayers(String query) {
        return profileRepository.findByDisplayNameContainingIgnoreCase(query).stream()
                .map(ProfileDto::from)
                .collect(Collectors.toList());
    }

    public void inviteFriend(String nickname) {
        String userId = SecurityContext.getUserId();
        String query = nickname == null ? "" : nickname.trim();
        if (query.isEmpty()) throw new RuntimeException("Enter a player name");

        Profile friend = profileRepository.findByDisplayNameContainingIgnoreCase(query).stream()
                .filter(p -> p.getDisplayName() != null && p.getDisplayName().equalsIgnoreCase(query))
                .findFirst()
                .orElseThrow(() -> new RuntimeException("Player not found"));

        if (friend.getId().equals(userId)) throw new RuntimeException("Cannot add yourself");

        var existing = friendshipRepository.findByUserIdAndFriendId(userId, friend.getId());
        if (existing.isPresent()) {
            String status = existing.get().getStatus();
            if ("ACCEPTED".equalsIgnoreCase(status)) {
                throw new RuntimeException("Already friends");
            }
            if ("PENDING".equalsIgnoreCase(status)) {
                throw new RuntimeException("Friend request already sent");
            }
        }

        var reverse = friendshipRepository.findByUserIdAndFriendId(friend.getId(), userId);
        if (reverse.isPresent() && "PENDING".equalsIgnoreCase(reverse.get().getStatus())) {
            Friendship f = reverse.get();
            f.setStatus("ACCEPTED");
            friendshipRepository.save(f);
            return;
        }

        Friendship f = new Friendship(userId, friend.getId(), "PENDING");
        friendshipRepository.save(f);
    }

    public long getPendingRequestCount() {
        String userId = SecurityContext.getUserId();
        return friendshipRepository.countPendingRequests(userId);
    }
}
