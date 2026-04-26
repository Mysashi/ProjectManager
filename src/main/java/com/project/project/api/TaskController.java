package com.project.project.api;

import com.project.project.domain.dto.request.create.CreateTaskRequestDto;
import com.project.project.domain.dto.request.update.UpdateTaskRequestDto;
import com.project.project.domain.dto.response.TaskResponseDto;
import com.project.project.domain.impl.TaskServiceImpl;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

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
    public TaskResponseDto updateTask(@RequestBody UpdateTaskRequestDto requestDto) {
        return taskService.update(requestDto);
    }
}
