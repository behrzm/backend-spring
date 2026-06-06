package com.codequest.repository;

import com.codequest.domain.entity.Friendship;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface FriendshipRepository extends JpaRepository<Friendship, Long> {
    Optional<Friendship> findByUserIdAndFriendId(String userId, String friendId);
    
    @Query("SELECT f FROM Friendship f WHERE ((f.userId = ?1 AND f.status = 'ACCEPTED') OR (f.friendId = ?1 AND f.status = 'ACCEPTED'))")
    List<Friendship> findAllAcceptedFriends(String userId);
    
    @Query("SELECT f FROM Friendship f WHERE f.friendId = ?1 AND f.status = ?2")
    List<Friendship> findByFriendIdAndStatus(String friendId, String status);
    
    @Query("SELECT COUNT(f) FROM Friendship f WHERE f.friendId = ?1 AND f.status = 'PENDING'")
    long countPendingRequests(String userId);
}
