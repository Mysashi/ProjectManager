package com.project.project.api;

import com.project.project.domain.dto.request.create.CreateTaskRequestDto;
import com.project.project.domain.dto.request.update.AssignTaskRequestDto;
import com.project.project.domain.dto.request.update.UpdateTaskRequestDto;
import com.project.project.domain.dto.response.ProjectResponseDto;
import com.project.project.domain.dto.response.TaskResponseDto;
import com.project.project.domain.impl.TaskServiceImpl;
import com.project.project.domain.service.TaskService;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.stereotype.Controller;

import java.util.HashMap;
import java.util.Map;

@Controller
public class TaskWebSocketController {

    private final TaskServiceImpl taskService;
    private final SimpMessagingTemplate messagingTemplate;

    public TaskWebSocketController(TaskServiceImpl taskService, SimpMessagingTemplate messagingTemplate) {
        this.taskService = taskService;
        this.messagingTemplate = messagingTemplate;
    }

    @PreAuthorize("@projectSecurity.hasProjectRole(#projectId, 'MANAGER', 'OWNER')")
    @MessageMapping("/project/{projectId}/task/create")
    public void createTask(@DestinationVariable Long projectId, @Payload CreateTaskRequestDto request) {
        TaskResponseDto task = taskService.create(request);
        messagingTemplate.convertAndSend("/topic/project/" + projectId + "/tasks", task);
    }

    @PreAuthorize("@projectSecurity.hasProjectRole(#projectId, 'MANAGER', 'OWNER')")
    @MessageMapping("/project/{projectId}/task/update")
    public void updateTask(@DestinationVariable Long projectId, @Payload UpdateTaskRequestDto request) {
        TaskResponseDto task = taskService.update(request);
        messagingTemplate.convertAndSend("/topic/project/" + projectId + "/tasks", task);
    }

    @PreAuthorize("@projectSecurity.hasProjectRole(#projectId, 'MANAGER', 'OWNER')")
    @MessageMapping("/project/{projectId}/task/assign")
    public void assignTask(@DestinationVariable Long projectId, @Payload AssignTaskRequestDto request) {
        TaskResponseDto task = taskService.assignTask(request);
        messagingTemplate.convertAndSend("/topic/project/" + projectId + "/tasks", task);
    }

    @PreAuthorize("@projectSecurity.hasProjectRole(#projectId, 'MANAGER', 'OWNER')")
    @MessageMapping("/project/{projectId}/task/delete/{taskId}")
    public void deleteTask(@DestinationVariable Long projectId, @DestinationVariable Long taskId) {
        taskService.delete(taskId);
        Map<String, Object> deleteNotification = new HashMap<>();
        deleteNotification.put("action", "DELETE");
        deleteNotification.put("taskId", taskId);
        messagingTemplate.convertAndSend("/topic/project/" + projectId + "/tasks", (Object) deleteNotification);
    }
}