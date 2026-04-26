package com.project.project.domain.service;

import com.project.project.domain.dto.request.create.CreateProjectRequestDto;
import com.project.project.domain.dto.request.update.UpdateProjectRequestDto;
import com.project.project.domain.dto.response.ProjectResponseDto;
import com.project.project.domain.dto.response.TaskResponseDto;


import java.util.List;

public interface ProjectService {

    ProjectResponseDto create(CreateProjectRequestDto request);

    ProjectResponseDto deleteProject(Long id);

    ProjectResponseDto getOneOrElseThrow(Long id);

    ProjectResponseDto update(UpdateProjectRequestDto request);

    ProjectResponseDto archive(Long id);

    List<TaskResponseDto> getTasksOfProject(Long id);
}
