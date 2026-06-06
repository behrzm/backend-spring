package com.codequest.repository;

import com.codequest.domain.entity.Profile;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FriendRepository extends JpaRepository<Profile, String> {
    
    @Query(value = "SELECT p.* FROM profiles p JOIN friends f ON p.id = f.friend_id WHERE f.user_id = ?1 AND f.status = 'active'", nativeQuery = true)
    List<Profile> findFriendsByUserId(String userId);

    @Query("SELECT p FROM Profile p WHERE LOWER(p.displayName) LIKE LOWER(CONCAT('%', ?1, '%')) AND p.id != ?2")
    List<Profile> searchProfiles(String query, String currentUserId);
}
