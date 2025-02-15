package com.project.Task.Manager;

import com.project.Task.Manager.Entities.Task;
import com.project.Task.Manager.Entities.User;
import com.project.Task.Manager.Repositories.TaskRepository;
import com.project.Task.Manager.Service.TaskService;
import com.project.Task.Manager.Service.UserService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class TaskServiceTest {
    @Mock
    private TaskRepository taskRepository;

    @Mock
    private UserService userService;

    @Mock
    private Jwt jwt;

    @InjectMocks
    private TaskService taskService;

    private User testUser;
    private Task testTask;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        testUser = new User();
        testUser.setUserId(1L);
        testUser.setUsername("testUser");
        testUser.setEmail("test@example.com");

        testTask = new Task();
        testTask.setTaskId(1L);
        testTask.setTitle("Test Task");
        testTask.setDescription("Test Description");
        testTask.setDueDate(LocalDate.now());
        testTask.setStatus("PENDING");
        testTask.setUser(testUser);
    }

    @Test
    void getTasksByUserId_ShouldReturnListOfTasks() {
        when(taskRepository.findByUser_UserId(testUser.getUserId()))
                .thenReturn(Arrays.asList(testTask));

        List<Task> result = taskService.getTasksByUserId(testUser.getUserId());

        assertNotNull(result);
        assertEquals(1, result.size());
        assertEquals(testTask.getTitle(), result.get(0).getTitle());
        verify(taskRepository).findByUser_UserId(testUser.getUserId());
    }

    @Test
    void createTask_ShouldReturnCreatedTask() {
        when(userService.findOrCreateUserByOAuthId(anyString(), any(Jwt.class)))
                .thenReturn(Optional.of(testUser));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task result = taskService.createTask(testTask, "oauth123", jwt);

        assertNotNull(result);
        assertEquals(testTask.getTitle(), result.getTitle());
        assertEquals(testTask.getUser().getUserId(), result.getUser().getUserId());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void updateTask_ShouldReturnUpdatedTask() {
        when(taskRepository.findByTaskIdAndUser_UserId(testTask.getTaskId(), testUser.getUserId()))
                .thenReturn(Optional.of(testTask));
        when(taskRepository.save(any(Task.class))).thenReturn(testTask);

        Task updatedTask = new Task();
        updatedTask.setTitle("Updated Title");
        updatedTask.setDescription("Updated Description");

        Task result = taskService.updateTask(testTask.getTaskId(), updatedTask, testUser.getUserId());

        assertNotNull(result);
        assertEquals(updatedTask.getTitle(), result.getTitle());
        assertEquals(updatedTask.getDescription(), result.getDescription());
        verify(taskRepository).save(any(Task.class));
    }

    @Test
    void deleteTask_ShouldDeleteSuccessfully() {
        when(taskRepository.findByTaskIdAndUser_UserId(testTask.getTaskId(), testUser.getUserId()))
                .thenReturn(Optional.of(testTask));

        taskService.deleteTask(testTask.getTaskId(), testUser.getUserId());

        verify(taskRepository).delete(testTask);
    }
}
