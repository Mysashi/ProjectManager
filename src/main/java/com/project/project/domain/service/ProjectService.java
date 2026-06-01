package com.project.project.domain.service;

import com.project.project.domain.dto.request.create.CreateProjectRequestDto;
import com.project.project.domain.dto.request.update.UpdateProjectRequestDto;
import com.project.project.domain.dto.response.ProjectMemberResponseDto;
import com.project.project.domain.dto.response.ProjectResponseDto;
import com.project.project.domain.dto.response.TaskResponseDto;
import com.project.project.domain.dto.response.UserResponseDto;


import java.util.List;

public interface ProjectService {

    ProjectResponseDto create(CreateProjectRequestDto request);

    ProjectResponseDto deleteProject(Long id);

    ProjectResponseDto getOneOrElseThrow(Long id);

    ProjectResponseDto update(UpdateProjectRequestDto request);

    ProjectResponseDto archive(Long id, Long daysToKeep);

    List<UserResponseDto> getAllUsersAttachedToProject(Long id);

    ProjectResponseDto joinProject(Long projectId, String username);

    ProjectResponseDto addParticipantToProject(Long userId, Long projectId);

    ProjectResponseDto deleteParticipantOfProject(Long userId, Long projectId);



}
