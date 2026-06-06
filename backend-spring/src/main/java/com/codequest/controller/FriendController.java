package com.codequest.controller;

import com.codequest.dto.FriendDto;
import com.codequest.dto.FriendRequestAction;
import com.codequest.dto.ProfileDto;
import com.codequest.service.FriendService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/friends")
@Tag(name = "Friends", description = "Friend management and search")
public class FriendController {

    private final FriendService friendService;

    public FriendController(FriendService friendService) {
        this.friendService = friendService;
    }

    @GetMapping
    @Operation(summary = "Get accepted friends list")
    public ResponseEntity<List<FriendDto>> getFriends() {
        return ResponseEntity.ok(friendService.getFriends());
    }

    @GetMapping("/requests")
    @Operation(summary = "Get pending friend requests")
    public ResponseEntity<List<FriendDto>> getRequests() {
        return ResponseEntity.ok(friendService.getPendingRequests());
    }

    @PostMapping("/handle-request")
    @Operation(summary = "Accept or decline a friend request")
    public ResponseEntity<Void> handleRequest(@RequestBody FriendRequestAction action) {
        friendService.handleRequest(action.getSenderId(), action.getAction());
        return ResponseEntity.ok().build();
    }

    @GetMapping("/search")
    @Operation(summary = "Search for players by nickname")
    public ResponseEntity<List<ProfileDto>> searchPeople(@RequestParam String query) {
        return ResponseEntity.ok(friendService.searchPlayers(query));
    }

    @PostMapping("/invite")
    @Operation(summary = "Send a friend request")
    public ResponseEntity<Void> inviteFriend(@RequestParam String nickname) {
        friendService.inviteFriend(nickname);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/pending-count")
    @Operation(summary = "Count incoming pending friend requests")
    public ResponseEntity<Long> getPendingCount() {
        return ResponseEntity.ok(friendService.getPendingRequestCount());
    }
}
