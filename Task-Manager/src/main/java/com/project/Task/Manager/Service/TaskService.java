package com.project.Task.Manager.Service;

import com.project.Task.Manager.Entities.Task;

import com.project.Task.Manager.Entities.User;
import com.project.Task.Manager.Repositories.TaskRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class TaskService {

    @Autowired
    private TaskRepository taskRepository;

    @Autowired
    private UserService userService;

    public List<Task> getTasksByUserId(Long userId) {
        return taskRepository.findByUser_UserId(userId);
    }

    public Task createTask(Task task, String oauthId, Jwt jwt) {
        User user = userService.findOrCreateUserByOAuthId(oauthId, jwt)
                .orElseThrow(() -> new RuntimeException("User not found"));

        user.setOauthId(oauthId);
        task.setUser(user);
        return taskRepository.save(task);
    }

    public Task updateTask(Long taskId, Task updatedTask, Long userId) {
        Task task = taskRepository.findByTaskIdAndUser_UserId(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        task.setTitle(updatedTask.getTitle());
        task.setDescription(updatedTask.getDescription());
        task.setDueDate(updatedTask.getDueDate());
        task.setStatus(updatedTask.getStatus());
        return taskRepository.save(task);
    }

    public void deleteTask(Long taskId, Long userId) {
        Task task = taskRepository.findByTaskIdAndUser_UserId(taskId, userId)
                .orElseThrow(() -> new RuntimeException("Task not found"));
        taskRepository.delete(task);
    }
}
