package com.project.project.domain.mapper;

import com.project.project.domain.dto.request.create.CreateTaskRequestDto;
import com.project.project.domain.dto.request.update.UpdateTaskRequestDto;
import com.project.project.domain.dto.response.TaskResponseDto;
import com.project.project.domain.entity.Task;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface TaskMapper {

    Task toEntity(CreateTaskRequestDto request);

    Task updateEntityFromDto(UpdateTaskRequestDto dto, @MappingTarget Task entity);

    @Mapping(source = "project.id", target = "projectId")
    @Mapping(source = "assignee.id", target = "assigneeId")
    TaskResponseDto toResponseDto(Task task);

}
