package com.project.project.domain.mapper;

import com.project.project.domain.dto.request.auth.LoginRequestDto;
import com.project.project.domain.dto.request.auth.RegisterRequestDto;
import com.project.project.domain.dto.request.create.CreateUserRequestDto;
import com.project.project.domain.dto.request.update.UpdateUserRequestDto;
import com.project.project.domain.dto.response.RegisterResponseDto;
import com.project.project.domain.dto.response.UserResponseDto;
import com.project.project.domain.entity.User;
import org.mapstruct.*;


@Mapper(componentModel = "spring", unmappedTargetPolicy = ReportingPolicy.IGNORE,
        nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE,
        nullValueCheckStrategy = NullValueCheckStrategy.ALWAYS
)
public interface UserMapper {

    User loginEntity(LoginRequestDto request);

    User registerEntity(RegisterRequestDto request);

    @Mapping(target = "refreshToken", source = "user.refreshToken.token")
    UserResponseDto toResponseDto(User user);


    RegisterResponseDto toRegisterResponseDto(User user);

    User updateEntityFromDto(UpdateUserRequestDto dto, @MappingTarget User entity);

}
