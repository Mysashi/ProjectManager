package com.project.project.api;

import com.project.project.domain.dto.request.create.CreateProjectRequestDto;
import com.project.project.domain.dto.request.update.UpdateProjectRequestDto;
import com.project.project.domain.dto.response.ProjectResponseDto;
import com.project.project.domain.dto.response.TaskResponseDto;
import com.project.project.domain.impl.ProjectServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectController {

    private final ProjectServiceImpl projectService;

    @Autowired
    ProjectController(ProjectServiceImpl projectService) {
        this.projectService = projectService;
    }

    @PostMapping
    public ProjectResponseDto createProject(@Valid @RequestBody CreateProjectRequestDto request) {
        return projectService.create(request);
    }

    @GetMapping("/delete/{id}")
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

    @GetMapping("/archive/{id}")
    public ProjectResponseDto archiveProject(@PathVariable Long id) {
        return projectService.archive(id);
    }

    @GetMapping("{id}/tasks")
    public List<TaskResponseDto> getTasksOfProject(@PathVariable Long id) {
        return projectService.getTasksOfProject(id);
    }

}
