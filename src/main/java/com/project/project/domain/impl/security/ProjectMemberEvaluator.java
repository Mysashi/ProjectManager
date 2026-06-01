package com.project.project.domain.impl.security;

import com.project.project.domain.entity.ProjectMember;
import com.project.project.domain.repo.ProjectMemberJpaRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Optional;

@Slf4j
@Component("projectSecurity")
public class ProjectMemberEvaluator {

    private final ProjectMemberJpaRepository projectMemberJpaRepository;

    @Autowired
    public ProjectMemberEvaluator(ProjectMemberJpaRepository projectMemberJpaRepository) {
        this.projectMemberJpaRepository = projectMemberJpaRepository;
    }

    public boolean hasProjectRole(Long projectId, String... allowedRoles) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            return false;
        }
        String username = authentication.getName();

        Optional<ProjectMember> participant = projectMemberJpaRepository.
                findByProject_IdAndUser_Username(projectId, username);

        if (participant.isEmpty()) {
            return false;
        }

        String userRoleInProject = participant.get().getRole().toString();
        log.info("Sent role: {}, userRole: {}", allowedRoles, userRoleInProject);
        boolean hasAccess = Arrays.stream(allowedRoles)
                .anyMatch(role -> role.equalsIgnoreCase(userRoleInProject));

        if (!hasAccess) {
            log.info("No access because of lack of roles");
            throw new AuthorizationDeniedException("Для этого действия требуется роль: " + Arrays.toString(allowedRoles));
        }
        return true;
    }

}
