package com.project.project.api;

import com.project.project.domain.dto.request.create.CreateProjectRequestDto;
import com.project.project.domain.dto.request.update.UpdateProjectRequestDto;
import com.project.project.domain.dto.response.ProjectResponseDto;
import com.project.project.domain.dto.response.UserResponseDto;
import com.project.project.domain.impl.ProjectServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectRestController {

    private final ProjectServiceImpl projectService;

    @Autowired
    ProjectRestController(ProjectServiceImpl projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ProjectResponseDto createProject(@Valid @RequestBody CreateProjectRequestDto request) {
        return projectService.create(request);
    }

    @DeleteMapping("/delete/{id}")
    public ProjectResponseDto deleteProject(@PathVariable Long id) {
        return projectService.deleteProject(id);
    }

    @GetMapping("/{id}")
    public ProjectResponseDto getOne(@PathVariable Long id) {
        return projectService.getOneOrElseThrow(id);
    }

    @PostMapping("/update")
    public ProjectResponseDto updateProject(@RequestBody UpdateProjectRequestDto request) {
        return projectService.update(request);
    }

    @PreAuthorize("@projectSecurity.hasProjectRole(#id, 'MANAGER', 'OWNER')")
    @PostMapping("/archive")
    public ProjectResponseDto archiveProject(Long id, Long daysToKeep) {
        return projectService.archive(id, daysToKeep);
    }

    @GetMapping("/all")
    public List<ProjectResponseDto> getAllProjects() {
        return projectService.findAll();
    }

//    @GetMapping("{id}/tasks")
//    public List<TaskResponseDto> getTasksOfProject(@PathVariable Long id) {
//        return projectService.getTasksOfProject(id);
//    }

    @GetMapping("/{id}/users")
    public List<UserResponseDto> getAllUsersAttachedToProject(@PathVariable Long id) {
        return  projectService.getAllUsersAttachedToProject(id);
    }

    @GetMapping("/join/{id}")
    public ProjectResponseDto joinProject(@PathVariable Long id, @AuthenticationPrincipal UserDetails userDetails) {
        return projectService.joinProject(id, userDetails.getUsername());
    }

    @PreAuthorize("@projectSecurity.hasProjectRole(#projectId, 'OWNER'")
    @GetMapping("/add")
    public ProjectResponseDto addParticipantToProject(Long userId, Long projectId) {
        return projectService.addParticipantToProject(userId, projectId);
    }

    @PreAuthorize("@projectSecurity.hasProjectRole(#projectId, 'OWNER'")
    @DeleteMapping("/delete")
    public ProjectResponseDto deleteParticipantToProject(Long userId, Long projectId) {
        return projectService.deleteParticipantOfProject(userId, projectId);
    }

}
