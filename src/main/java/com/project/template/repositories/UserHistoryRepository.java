package com.project.template.repositories;

import com.project.template.domain.entities.UserHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserHistoryRepository extends JpaRepository<UserHistory, Long> {
    @Query("SELECT uh FROM UserHistory uh WHERE uh.user.id = :userId")
    Optional<UserHistory> findByUser(Long userId);
}
