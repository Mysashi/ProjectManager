package com.project.project.domain.impl;

import com.project.project.domain.dto.request.create.CreateProjectRequestDto;
import com.project.project.domain.dto.request.update.UpdateProjectRequestDto;
import com.project.project.domain.dto.response.ProjectMemberResponseDto;
import com.project.project.domain.dto.response.ProjectResponseDto;
import com.project.project.domain.dto.response.TaskResponseDto;
import com.project.project.domain.dto.response.UserResponseDto;
import com.project.project.domain.entity.ProjectMember;
import com.project.project.domain.entity.ProjectRole;
import com.project.project.domain.entity.User;
import com.project.project.domain.entity.project.Project;
import com.project.project.domain.entity.project.ProjectStatus;
import com.project.project.domain.mapper.ProjectMapper;
import com.project.project.domain.mapper.TaskMapper;
import com.project.project.domain.mapper.UserMapper;
import com.project.project.domain.repo.ProjectJpaRepository;
import com.project.project.domain.repo.ProjectMemberJpaRepository;
import com.project.project.domain.repo.UserJpaRepository;
import com.project.project.domain.service.ProjectService;
import jakarta.persistence.EntityExistsException;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
public class ProjectServiceImpl implements ProjectService {
    private final ProjectMapper projectMapper;
    private final ProjectJpaRepository projectJpaRepository;
    private final TaskMapper taskMapper;
    private final UserJpaRepository userJpaRepository;
    private final ProjectMemberJpaRepository projectMemberJpaRepository;
    private final UserMapper userMapper;
    private final UserServiceImpl userService;


    @Autowired
    ProjectServiceImpl(ProjectMapper mapper, ProjectJpaRepository projectJpaRepository, TaskMapper taskMapper, UserJpaRepository userJpaRepository, ProjectMemberJpaRepository projectMemberJpaRepository, UserMapper userMapper, UserServiceImpl userService) {
        this.projectMapper = mapper;
        this.projectJpaRepository = projectJpaRepository;
        this.taskMapper = taskMapper;
        this.userJpaRepository = userJpaRepository;
        this.projectMemberJpaRepository = projectMemberJpaRepository;
        this.userMapper = userMapper;
        this.userService = userService;
    }

    @Override
    @Transactional
    public ProjectResponseDto create(CreateProjectRequestDto request) {
        if (projectJpaRepository.existsByName(request.name())) {
            throw new EntityExistsException("Project with name " + request.name() + " already exists");
        }

        var entity = projectMapper.toEntity(request);
        entity.setStatus(ProjectStatus.ACTIVE);
        entity.setDateOfCreation(Date.from(Instant.now()));

        var savedProject = projectJpaRepository.save(entity);

        // 2. Получаем пользователя
        String username = SecurityContextHolder.getContext().getAuthentication().getName();
        User user = userJpaRepository.findByUsername(username)
                .orElseThrow(() -> new RuntimeException("User not found"));

        linkProjectMember(savedProject, user, ProjectRole.OWNER);

        log.info("Project was successfully created! id={}", savedProject.getId());

        return projectMapper.toResponseDto(savedProject);
    }

    public List<ProjectResponseDto> findAll() {
        return projectJpaRepository.findAll().stream()
                .map(project -> new ProjectResponseDto(
                        project.getId(),
                        project.getName(),
                        project.getDescription(),
                        project.getStatus().toString(),
                        project.getDateOfCreation(),
                        project.getDateOfDeletion(),
                        // Маппим список сущностей в список DTO
                        project.getProjectMembers().stream()
                                .map(member -> new ProjectMemberResponseDto(
                                        member.getId(),
                                        member.getUser().getUsername(), // Достаем имя пользователя
                                        member.getRole().toString()     // Достаем роль
                                ))
                                .toList()
                ))
                .toList();
    }

    private void linkProjectMember(Project entity, User user, ProjectRole role) {
        ProjectMember member = new ProjectMember();
        member.setProject(entity);
        member.setUser(user);
        member.setRole(role);
        projectMemberJpaRepository.save(member);
        entity.addMemberInProject(member);
    }

    @Override
    public ProjectResponseDto deleteProject(Long id) {
        var found = findProject(id);
        projectJpaRepository.delete(found);
        log.info("Project was successfully deleted! id={}", id);

        return projectMapper.toResponseDto(found);
    }

    @Override
    @Transactional
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
    @Transactional
    public ProjectResponseDto update(UpdateProjectRequestDto request) {
        var found = findProject(request.id());
        var entity = projectMapper.updateEntityFromDto(request, found);
        log.info("Project was updated! id={}", found.getId());
        var saved = projectJpaRepository.save(entity);

        return projectMapper.toResponseDto(saved);
    }

    @Transactional
    @Override
    public ProjectResponseDto archive(Long id, Long daysToKeep) {
        var found = findProject(id);
        found.setStatus(ProjectStatus.ARCHIVED);
        found.setDateOfDeletion(LocalDateTime.now().plusDays(daysToKeep));
        log.info("Project was archived! id={} with daysToKeep={}", found.getId(), daysToKeep);
        var saved = projectJpaRepository.save(found);
        return projectMapper.toResponseDto(saved);
    }

//    @Override
//    public List<TaskResponseDto> getTasksOfProject(Long id) {
//        log.info("Project tasks were retrieved! id={}", id);
//        var found = findProject(id);
//        return found.getTasks().stream().map(taskMapper::toResponseDto).toList();
//    }

    @Override
    public List<UserResponseDto> getAllUsersAttachedToProject(Long projectId) {
        var entity = findProject(projectId);
        var projectMembers= entity.getProjectMembers();
        return projectMembers.stream().map(p -> userMapper.toResponseDto(p.getUser())).toList();
    }


    @Override
    @Transactional
    public ProjectResponseDto joinProject(Long projectId, String username) {
        var project = findProject(projectId);
        var user = userService.findUserByUsername(username);
        if (projectMemberJpaRepository.existsByProjectAndUserUsername(project, username)) {
            throw new EntityExistsException("User '" + username + "' is already a member of project ID " + projectId);
        }
        log.info("User={} joined the project with id={}", username, projectId);
        linkProjectMember(project, user, ProjectRole.MEMBER);
        return projectMapper.toResponseDto(project);
    }

    @Override
    public ProjectResponseDto addParticipantToProject(Long userId, Long projectId) {
        var project = findProject(projectId);
        var user = userService.findUser(userId);
        if (projectMemberJpaRepository.existsByProjectAndUserUsername(project, user.getUsername())) {
            throw new EntityExistsException("User '" +
                    user.getUsername() + "' is already a member of project ID " + projectId);
        }
        log.info("User with id={} was added successfully!", user.getId());
        linkProjectMember(project, user, ProjectRole.MEMBER);
        return projectMapper.toResponseDto(project);
    }

    @Override
    @Transactional
    public ProjectResponseDto deleteParticipantOfProject(Long userId, Long projectId) {
        var project = findProject(projectId);
        var user = userService.findUser(userId);
        if (!projectMemberJpaRepository.existsByProjectAndUserUsername(project, user.getUsername())) {
            throw new EntityExistsException("User '" +
                    user.getUsername() + "' is not a member of project ID " + projectId);
        }
        var foundProjectMember = projectMemberJpaRepository.findByProject_IdAndUser_Id(projectId, userId);
        if (foundProjectMember.isEmpty()) {
            throw new EntityExistsException("Inner error of projectMember " + projectId);
        }
        projectMemberJpaRepository.delete(foundProjectMember.get());
        project.getProjectMembers().removeIf(m -> m.getUser().getId().equals(userId));
        log.info("User with id={} was removed successfully!", user.getId());
        return projectMapper.toResponseDto(project);
    }


}
