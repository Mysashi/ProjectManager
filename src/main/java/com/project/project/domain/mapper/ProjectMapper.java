package com.project.project.domain.mapper;

import com.project.project.domain.dto.request.create.CreateProjectRequestDto;
import com.project.project.domain.dto.request.update.UpdateProjectRequestDto;
import com.project.project.domain.dto.response.ProjectMemberResponseDto;
import com.project.project.domain.dto.response.ProjectResponseDto;
import com.project.project.domain.entity.ProjectMember;
import com.project.project.domain.entity.project.Project;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProjectMapper {

    Project toEntity(CreateProjectRequestDto request);

    Project updateEntityFromDto(UpdateProjectRequestDto dto, @MappingTarget Project entity);

    @Mapping(target = "members", source = "projectMembers")
    ProjectResponseDto toResponseDto(Project project);

    @Mapping(target = "id", source = "user.id")
    @Mapping(target = "name", source = "user.username") // Указываем, откуда брать имя участника
    @Mapping(target = "projectRole", source = "role")   // Указываем, откуда брать роль
    ProjectMemberResponseDto toMemberDto(ProjectMember member);
}