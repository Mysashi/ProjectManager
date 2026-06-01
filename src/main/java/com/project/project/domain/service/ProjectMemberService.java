package com.project.project.domain.service;

import com.project.project.domain.dto.request.update.TransferProjectRequestDto;
import com.project.project.domain.dto.response.ProjectMemberResponseDto;
import com.project.project.domain.dto.response.ProjectResponseDto;
import com.project.project.domain.entity.ProjectRole;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.List;


public interface ProjectMemberService {

    ProjectMemberResponseDto transferOwnerRightsOfProject(String username,
                                                          TransferProjectRequestDto transferProjectRequestDto);
    List<ProjectMemberResponseDto> findProjectsByUsername(String username);
    ProjectResponseDto assignUserRole(String username, Long userId, Long projectId , ProjectRole newRole);
}
