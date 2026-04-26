package com.project.project.domain.impl;

import com.project.project.domain.dto.request.create.CreateProjectRequestDto;
import com.project.project.domain.dto.request.update.UpdateProjectRequestDto;
import com.project.project.domain.dto.response.ProjectResponseDto;
import com.project.project.domain.dto.response.TaskResponseDto;
import com.project.project.domain.entity.Task;
import com.project.project.domain.entity.project.Project;
import com.project.project.domain.entity.project.ProjectStatus;
import com.project.project.domain.mapper.ProjectMapper;
import com.project.project.domain.mapper.TaskMapper;
import com.project.project.domain.repo.ProjectJpaRepository;
import com.project.project.domain.service.ProjectService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {


    private final ProjectMapper projectMapper;
    private final ProjectJpaRepository projectJpaRepository;
    private final TaskMapper taskMapper;

    @Autowired
    ProjectServiceImpl(ProjectMapper mapper, ProjectJpaRepository projectJpaRepository, TaskMapper taskMapper) {
        this.projectMapper = mapper;
        this.projectJpaRepository = projectJpaRepository;
        this.taskMapper = taskMapper;
    }

    @Override
    public ProjectResponseDto create(CreateProjectRequestDto request) {
        var entity = projectMapper.toEntity(request);
        var currentDate = Date.from(Instant.now());
        if (projectJpaRepository.existsByName(request.name())) {
            throw new EntityExistsException("Project with name " + request.name() + " already exists");
        }
        entity.setStatus(ProjectStatus.ACTIVE);
        entity.setDateOfCreation(currentDate);
        var saved = projectJpaRepository.save(entity);
        log.info("Project was successfully created! id={}", saved.getId());

        return projectMapper.toResponseDto(saved);
    }

    @Override
    public ProjectResponseDto deleteProject(Long id) {
        var found = findProject(id);
        projectJpaRepository.delete(found);
        log.info("Project was successfully deleted! id={}", id);

        return projectMapper.toResponseDto(found);
    }

    @Override
    public ProjectResponseDto getOneOrElseThrow(Long id) {
        var found = findProject(id);

        return projectMapper.toResponseDto(found);
    }

    public Project findProject(Long id) {
        var found = projectJpaRepository.findById(id);
        if (found.isEmpty()) {
            throw new EntityNotFoundException("Project with id " + id + " not found");
        }
        log.info("Project was found! id={}", id);

        return found.get();
    }

    @Override
    public ProjectResponseDto update(UpdateProjectRequestDto request) {
        var found = findProject(request.id());
        var entity = projectMapper.updateEntityFromDto(request, found);
        log.info("Project was updated! id={}", found.getId());
        var saved = projectJpaRepository.save(entity);

        return projectMapper.toResponseDto(saved);
    }

    @Transactional
    @Override
    public ProjectResponseDto archive(Long id) {
        var found = findProject(id);
        log.info("Project was archived! id={}", found.getId());
        projectJpaRepository.updateStatus(id, ProjectStatus.ARCHIVED);

        return projectMapper.toResponseDto(found);
    }

    @Override
    public List<TaskResponseDto> getTasksOfProject(Long id) {
        log.info("Project tasks were retrieved! id={}", id);
        var found = findProject(id);
        return found.getTasks().stream().map(taskMapper::toResponseDto).toList();
    }

}
