package com.project.project;

import com.project.project.domain.dto.request.update.TransferProjectRequestDto;
import com.project.project.domain.dto.response.ProjectMemberResponseDto;
import com.project.project.domain.entity.ProjectMember;
import com.project.project.domain.entity.ProjectRole;
import com.project.project.domain.entity.User;
import com.project.project.domain.impl.ProjectMemberServiceImpl;
import com.project.project.domain.impl.UserServiceImpl;
import com.project.project.domain.mapper.ProjectMemberMapper;
import com.project.project.domain.repo.ProjectMemberJpaRepository;
import com.project.project.domain.service.ProjectMemberService;
import com.project.project.domain.service.ProjectService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class ProjectMemberMockitoTests {

    @Mock
    private ProjectMemberJpaRepository projectMemberJpaRepository;

    @Mock
    private ProjectMemberMapper projectMemberMapper;

    @Mock
    private UserServiceImpl userServiceImpl;

    @InjectMocks
    private ProjectMemberServiceImpl projectMemberService;



//    @Test
//    void assignUserRole_ShouldUpdateRole_WhenAllValid() {
//        Long userId = 2L;
//        Long projectId = 1L;
//        ProjectRole newRole = ProjectRole.DEVELOPER;
//        String ownerUsername = "currentOwner";
//
//        UserDetails userDetails = mock(UserDetails.class);
//        when(userDetails.getUsername()).thenReturn(ownerUsername);
//
//        ProjectMember ownerMember = new ProjectMember();
//        ProjectMember targetMember = new ProjectMember();
//        targetMember.setRole(ProjectRole.VIEWER);
//
//        ProjectMemberResponseDto expectedDto = new ProjectMemberResponseDto(
//                1L,
//                "Test User",
//                "Test Description",
//                "ACTIVE",
//                new java.util.Date(),
//                newRole.name()
//        );
//
//
//        when(projectMemberJpaRepository.findByProject_IdAndUser_Username(projectId, ownerUsername))
//                .thenReturn(Optional.of(ownerMember));
//        when(projectMemberJpaRepository.findByProject_IdAndUser_Id(projectId, userId))
//                .thenReturn(Optional.of(targetMember));
//        when(projectMemberJpaRepository.save(targetMember))
//                .thenReturn(targetMember);
//        when(projectMemberMapper.fromMemberToResponseDto(targetMember))
//                .thenReturn(expectedDto);
//
//
//        ProjectMemberResponseDto result = projectMemberService.assignUserRole(userDetails, userId, projectId, newRole);
//        assertNotNull(result);
//        assertEquals(newRole, targetMember.getRole());
//        verify(projectMemberJpaRepository, times(1)).save(targetMember);
//        verify(projectMemberMapper).fromMemberToResponseDto(targetMember);
//    }

}



