package com.project.project.domain.service;

import com.project.project.domain.dto.request.create.CreateTaskRequestDto;
import com.project.project.domain.dto.request.update.UpdateTaskRequestDto;
import com.project.project.domain.dto.response.TaskResponseDto;

public interface TaskService {

    TaskResponseDto create(CreateTaskRequestDto request);

    TaskResponseDto getOneOrElseThrow(Long id);

    TaskResponseDto delete(Long id);

    TaskResponseDto update(UpdateTaskRequestDto request);
}
