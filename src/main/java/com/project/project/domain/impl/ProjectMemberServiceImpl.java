package com.project.project.domain.impl;

import com.project.project.domain.dto.request.update.TransferProjectRequestDto;
import com.project.project.domain.dto.response.ProjectMemberResponseDto;
import com.project.project.domain.dto.response.ProjectResponseDto;
import com.project.project.domain.entity.ProjectRole;
import com.project.project.domain.mapper.ProjectMapper;
import com.project.project.domain.mapper.ProjectMemberMapper;
import com.project.project.domain.repo.ProjectJpaRepository;
import com.project.project.domain.repo.ProjectMemberJpaRepository;
import com.project.project.domain.service.ProjectMemberService;
import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
public class ProjectMemberServiceImpl implements ProjectMemberService {

    private final ProjectMemberJpaRepository projectMemberJpaRepository;
    private final ProjectMemberMapper projectMemberMapper;
    private final ProjectMapper projectMapper;
    private final ProjectJpaRepository projectJpaRepository;
    private final UserServiceImpl userServiceImpl;

    @Autowired
    public ProjectMemberServiceImpl(ProjectMemberJpaRepository projectMemberJpaRepository,
                                    ProjectMemberMapper projectMemberMapper, ProjectMapper projectMapper, ProjectJpaRepository projectJpaRepository,
                                    UserServiceImpl userServiceImpl) {
        this.projectMemberJpaRepository = projectMemberJpaRepository;
        this.projectMemberMapper = projectMemberMapper;
        this.projectMapper = projectMapper;
        this.projectJpaRepository = projectJpaRepository;
        this.userServiceImpl = userServiceImpl;
    }

    public List<ProjectMemberResponseDto> findProjectsByUsername(String username) {
        return projectMemberJpaRepository.findAllByUser_Username(username)
                .stream()
                .map(projectMemberMapper::fromMemberToResponseDto)
                .toList();
    }

    @Override
    @Transactional
    public ProjectResponseDto assignUserRole(String username, Long userId, Long projectId, ProjectRole newRole) {
        // 1. Проверки остаются прежними
        var assigningUser = projectMemberJpaRepository.findByProject_IdAndUser_Id(projectId, userId);
        if (assigningUser.isEmpty()) {
            throw new EntityNotFoundException("This user is not found in chosen project");
        }

        // 2. Обновляем роль
        assigningUser.get().setRole(newRole);
        projectMemberJpaRepository.save(assigningUser.get());

        log.info("Role was assigned={} to userId={}", newRole, userId);

        // 3. Возвращаем весь проект целиком
        var project = projectJpaRepository.findById(projectId)
                .orElseThrow(() -> new EntityNotFoundException("Project not found"));

        return projectMapper.toResponseDto(project);
    }

    @Transactional
    @Override
    public ProjectMemberResponseDto transferOwnerRightsOfProject(String username,
                                                                 TransferProjectRequestDto transferProjectRequestDto) {
        var projectId = transferProjectRequestDto.projectId();
        var userSourceId = userServiceImpl.findUserByUsername(username).getId();
        var userDestId = transferProjectRequestDto.userDestId();
        if (userSourceId.equals(userDestId)) {
            throw new IllegalArgumentException("Нельзя передать права самому себе");
        }
        var owner = projectMemberJpaRepository.findByProject_IdAndUser_IdAndRole(projectId, userSourceId, ProjectRole.OWNER);
        if (owner.isEmpty()) {
            throw new EntityNotFoundException("This user is not owner of project");
        }
        var userDest = projectMemberJpaRepository.findByProject_IdAndUser_Id(projectId, userDestId);
        if (userDest.isEmpty()) {
            throw new EntityNotFoundException("The user appointed to get owner rights is not found in project");
        }
        owner.get().setRole(ProjectRole.MEMBER);
        projectMemberJpaRepository.save(owner.get());
        userDest.get().setRole(ProjectRole.OWNER);
        log.info("The project was transferred from userSourceId={} to userDestId={}", userSourceId, userDestId);
        var saved = projectMemberJpaRepository.save(userDest.get());
        projectMemberJpaRepository.flush();
        return projectMemberMapper.fromMemberToResponseDto(saved);
    }
}
