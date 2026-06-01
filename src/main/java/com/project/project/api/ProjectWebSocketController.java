package com.project.project.api;

import com.project.project.domain.dto.request.create.CreateProjectRequestDto;
import com.project.project.domain.dto.request.update.TransferProjectRequestDto;
import com.project.project.domain.dto.request.update.UpdateProjectRequestDto;
import com.project.project.domain.dto.response.ProjectMemberResponseDto;
import com.project.project.domain.dto.response.ProjectResponseDto;
import com.project.project.domain.entity.ProjectRole;
import com.project.project.domain.impl.ProjectMemberServiceImpl;
import com.project.project.domain.impl.ProjectServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;

import java.security.Principal;
import java.util.Map;

@Controller
public class ProjectWebSocketController {

    private final ProjectServiceImpl projectService;
    private final ProjectMemberServiceImpl projectMemberService;
    private final SimpMessagingTemplate messagingTemplate;

    @Autowired
    public ProjectWebSocketController(ProjectServiceImpl projectService, ProjectMemberServiceImpl projectMemberService, SimpMessagingTemplate messagingTemplate) {
        this.projectService = projectService;
        this.projectMemberService = projectMemberService;
        this.messagingTemplate = messagingTemplate;
    }

    @MessageMapping("/project/{id}/join")
    public void joinProject(@DestinationVariable Long id, Principal principal) {
        ProjectResponseDto response = projectService.joinProject(id, principal.getName());
        messagingTemplate.convertAndSend("/topic/project/" + id, response);
    }

    @PreAuthorize("@projectSecurity.hasProjectRole(#projectId, 'OWNER')")
    @MessageMapping("/project/{projectId}/add-participant")
    public void addParticipant(Long userId, @DestinationVariable Long projectId) {
        ProjectResponseDto response = projectService.addParticipantToProject(userId, projectId);
        messagingTemplate.convertAndSend("/topic/project/" + projectId, response);
    }

    @PreAuthorize("@projectSecurity.hasProjectRole(#request.id, 'MANAGER', 'OWNER')")
    @MessageMapping("/project/update")
    public void updateProject(@Payload UpdateProjectRequestDto request) {
        // 1. Выполняем обновление через сервис
        ProjectResponseDto updatedProject = projectService.update(request);

        // 2. Рассылаем обновленный объект всем подписанным на проект
        messagingTemplate.convertAndSend("/topic/project/" + request.id(), updatedProject);
    }

    @PreAuthorize("@projectSecurity.hasProjectRole(#projectId, 'OWNER')")
    @MessageMapping("/project/{projectId}/delete-participant")
    public void deleteParticipant(Long userId, @DestinationVariable Long projectId) {
        ProjectResponseDto response = projectService.deleteParticipantOfProject(userId, projectId);
        messagingTemplate.convertAndSend("/topic/project/" + projectId, response);
    }

    @PreAuthorize("@projectSecurity.hasProjectRole(#id, 'MANAGER', 'OWNER')")
    @MessageMapping("/project/{id}/archive")
    public void archiveProject(@DestinationVariable Long id, @Payload Map<String, Object> payload) {
        Integer daysToKeep = (Integer) payload.getOrDefault("daysToKeep", 30);
        ProjectResponseDto response = projectService.archive(id, Long.valueOf(daysToKeep));
        messagingTemplate.convertAndSend("/topic/project/" + id, response);
    }

    @MessageMapping("/project.create")
    @SendTo("/topic/projects/new")
    public ProjectResponseDto createProjectViaWs(@Payload CreateProjectRequestDto request) {
        return projectService.create(request);
    }

    @MessageMapping("/project/{id}/transfer")
    public void transferOwner(@DestinationVariable Long id,
                              @Payload TransferProjectRequestDto dto,
                              Principal principal) {

        String username = principal.getName();

        projectMemberService.transferOwnerRightsOfProject(username, dto);

        ProjectResponseDto updatedProject = projectService.getOneOrElseThrow(id);

        messagingTemplate.convertAndSend("/topic/project/" + id, updatedProject);
    }

    @PreAuthorize("@projectSecurity.hasProjectRole(#projectId, 'OWNER')")
    @MessageMapping("/project/{projectId}/assign-role")
    public void assignUserRole(@Payload Map<String, Object> payload, @DestinationVariable Long projectId,
                               Principal principal) {
        Long userId = Long.valueOf(payload.get("userId").toString());
        ProjectRole newRole = ProjectRole.valueOf(payload.get("newRole").toString());
        ProjectResponseDto updatedProject = projectMemberService.
                assignUserRole(principal.getName(), userId, projectId, newRole);

        messagingTemplate.convertAndSend("/topic/project/" + projectId, updatedProject);
    }
}
