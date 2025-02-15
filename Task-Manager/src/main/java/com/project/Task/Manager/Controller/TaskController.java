package com.project.Task.Manager.Controller;

import com.project.Task.Manager.Entities.Task;
import com.project.Task.Manager.Entities.User;
import com.project.Task.Manager.Service.TaskService;
import com.project.Task.Manager.Service.UserService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

    @Autowired
    private TaskService taskService;

    @Autowired
    private UserService userService;

    @GetMapping("/getTasks")
    public ResponseEntity<?> getTasks(@AuthenticationPrincipal Jwt jwt) {
        try {
            String oauthId = jwt.getSubject();
            User user = userService.findOrCreateUserByOAuthId(oauthId, jwt)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            List<Task> tasks = taskService.getTasksByUserId(user.getUserId());
            return ResponseEntity.ok(tasks);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PostMapping("/createTask")
    public ResponseEntity<?> createTask(@Valid @RequestBody Task task, @AuthenticationPrincipal Jwt jwt) {
        try {
            String userId = jwt.getSubject();
            Task createdTask = taskService.createTask(task, userId, jwt);
            return ResponseEntity.ok(createdTask);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @PutMapping("/{taskId}")
    public ResponseEntity<?> updateTask(@PathVariable Long taskId, @Valid @RequestBody Task updatedTask, @AuthenticationPrincipal Jwt jwt) {
        try {
            String oauthId = jwt.getSubject();
            User user = userService.findOrCreateUserByOAuthId(oauthId, jwt)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            Task task = taskService.updateTask(taskId, updatedTask, user.getUserId());
            return ResponseEntity.ok(task);
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

    @DeleteMapping("/{taskId}")
    public ResponseEntity<?> deleteTask(@PathVariable Long taskId, @AuthenticationPrincipal Jwt jwt) {
        try {
            String oauthId = jwt.getSubject();
            User user = userService.findOrCreateUserByOAuthId(oauthId, jwt)
                    .orElseThrow(() -> new RuntimeException("User not found"));
            taskService.deleteTask(taskId, user.getUserId());
            return ResponseEntity.ok("Task deleted successfully!");
        } catch (Exception e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }
}
