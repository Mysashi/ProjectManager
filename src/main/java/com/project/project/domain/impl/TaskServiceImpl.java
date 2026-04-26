package com.project.project.domain.impl;

import com.project.project.domain.dto.request.create.CreateTaskRequestDto;
import com.project.project.domain.dto.request.update.UpdateTaskRequestDto;
import com.project.project.domain.dto.response.TaskResponseDto;
import com.project.project.domain.entity.Task;
import com.project.project.domain.entity.TaskStatus;
import com.project.project.domain.mapper.TaskMapper;
import com.project.project.domain.repo.TaskJpaRepository;
import com.project.project.domain.service.TaskService;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;

@Service
@Slf4j
public class TaskServiceImpl implements TaskService {

    private final TaskJpaRepository taskJpaRepository;
    private final TaskMapper mapper;
    private final ProjectServiceImpl projectService;

    @Autowired
    TaskServiceImpl(TaskJpaRepository taskJpaRepository, TaskMapper mapper, ProjectServiceImpl projectService) {

        this.taskJpaRepository = taskJpaRepository;
        this.mapper = mapper;
        this.projectService = projectService;
    }

    @Override
    public TaskResponseDto create(CreateTaskRequestDto request) {
        var saved = createTaskAndSave(request);
        log.info("Task was created! taskid={}, projectId={}", saved.id(), saved.projectId());

        return saved;
    }

    @Override
    public TaskResponseDto getOneOrElseThrow(Long id) {
        var found = findTask(id);

        return mapper.toResponseDto(found);
    }

    public Task findTask(Long id) {
        var found = taskJpaRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Task with id " + id + " not found");
        }
        log.info("Task was found! taskId={}, projectId={}", found.get().getId(), found.get().getProject().getId());

        return found.get();
    }

    @Override
    public TaskResponseDto delete(Long id) {
        var found = findTask(id);
        taskJpaRepository.delete(found);
        log.info("Task was successfully deleted! taskId={}, projectId={}", found.getId(), found.getProject().getId());

        return mapper.toResponseDto(found);
    }

    @Override
    public TaskResponseDto update(UpdateTaskRequestDto request) {
        var found = findTask(request.id());
        var entity = mapper.updateEntityFromDto(request, found);
        var saved = taskJpaRepository.save(entity);
        log.info("Task was updated! taskId={}, projectId={}", found.getId(), found.getProject().getId());
        return mapper.toResponseDto(saved);
    }

    private TaskResponseDto createTaskAndSave(CreateTaskRequestDto request) {
        var entity = mapper.toEntity(request);
        var project = projectService.findProject(request.projectId());
        var currentDate = Date.from(Instant.now());
        entity.setStatus(TaskStatus.OPENED);
        entity.setDateOfCreation(currentDate);
        entity.setProject(project);
        project.addTask(entity);

        var savedEntity = taskJpaRepository.save(entity);

        return mapper.toResponseDto(savedEntity);
    }


}
