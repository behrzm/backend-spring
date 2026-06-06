package com.codequest.repository;

import com.codequest.domain.entity.Profile;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface ProfileRepository extends JpaRepository<Profile, String> {
    
    @Query("SELECT p FROM Profile p ORDER BY p.xp DESC, p.level DESC")
    List<Profile> findTopPlayers(Pageable pageable);

    Optional<Profile> findById(String id);

    List<Profile> findByDisplayNameContainingIgnoreCase(String displayName);
}
