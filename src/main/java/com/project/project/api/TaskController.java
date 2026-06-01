package com.project.project.api;

import com.project.project.domain.dto.request.create.CreateTaskRequestDto;
import com.project.project.domain.dto.request.update.AssignTaskRequestDto;
import com.project.project.domain.dto.request.update.UpdateTaskRequestDto;
import com.project.project.domain.dto.response.TaskResponseDto;
import com.project.project.domain.impl.TaskServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/task")
public class TaskController {

    private final TaskServiceImpl taskService;

    @Autowired
    TaskController(TaskServiceImpl taskService) {
        this.taskService = taskService;
    }

    @PostMapping
    public TaskResponseDto createTask(@Valid @RequestBody CreateTaskRequestDto request) {
        return taskService.create(request);
    }

    @GetMapping("/{id}")
    public TaskResponseDto getOne(@PathVariable Long id) {
        return taskService.getOneOrElseThrow(id);
    }

    @PostMapping("/update")
    public TaskResponseDto updateTask(@RequestBody UpdateTaskRequestDto request) {
        return taskService.update(request);
    }

    @GetMapping("/{projectId}/tasks")
    public List<TaskResponseDto> getTasks(@PathVariable Long projectId) {
        return taskService.findAllByProjectId(projectId);
    }

    @PostMapping("/assign")
    public TaskResponseDto assignTask(@RequestBody AssignTaskRequestDto request) {
        return taskService.assignTask(request);
    }

    @DeleteMapping("/{id}")
    public TaskResponseDto deleteTask(@PathVariable Long id) {
        return taskService.delete(id);
    }
}
