package com.project.project.domain.mapper;

import com.project.project.domain.dto.response.ProjectMemberResponseDto;
import com.project.project.domain.dto.response.ProjectResponseDto;
import com.project.project.domain.entity.ProjectMember;
import org.mapstruct.*;

@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface ProjectMemberMapper {

    @Mapping(source = "user.id", target = "id")
    @Mapping(source = "user.username", target = "name")
    @Mapping(source = "role", target = "projectRole")
    ProjectMemberResponseDto fromMemberToResponseDto(ProjectMember member);
}
