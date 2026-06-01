package com.project.project.api;

import com.project.project.domain.dto.request.update.TransferProjectRequestDto;
import com.project.project.domain.dto.response.ProjectMemberResponseDto;
import com.project.project.domain.dto.response.ProjectResponseDto;
import com.project.project.domain.entity.ProjectRole;
import com.project.project.domain.impl.ProjectMemberServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/project")
public class ProjectMemberController {

    private final ProjectMemberServiceImpl projectMemberService;

    @Autowired
    ProjectMemberController(ProjectMemberServiceImpl projectMemberService) {
        this.projectMemberService = projectMemberService;
    }

    @PostMapping("/owned")
    public List<ProjectMemberResponseDto> getOwnedProjectsOfCurrentUser(@AuthenticationPrincipal UserDetails currentUser) {
        return projectMemberService.findProjectsByUsername(currentUser.getUsername());
    }

    @PreAuthorize("@projectSecurity.hasProjectRole(#transferProjectRequestDto.projectId, 'OWNER')")
    @PostMapping("/transfer")
    public ProjectMemberResponseDto transferOwnerRightsOfProject(
            @RequestBody TransferProjectRequestDto transferProjectRequestDto, @AuthenticationPrincipal UserDetails userDetails ) {
        return projectMemberService.transferOwnerRightsOfProject(userDetails.getUsername(), transferProjectRequestDto);
    }

    @PostMapping("/assignRole")
    public ProjectResponseDto assignUserRole(@AuthenticationPrincipal UserDetails userDetails,
                                             Long userId, Long projectId, ProjectRole newRole) {
        return projectMemberService.assignUserRole(userDetails.getUsername(), userId, projectId, newRole);
    }
}
