package com.project.Task.Manager.Repositories;

import com.project.Task.Manager.Entities.Task;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface TaskRepository extends JpaRepository<Task, Long> {
    @Query("SELECT t FROM Task t WHERE t.user.userId = :userId")
    List<Task> findByUser_UserId(Long userId);

    Optional<Task> findByTaskIdAndUser_UserId(Long taskId, Long userId);
}
