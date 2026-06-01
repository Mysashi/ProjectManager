package com.project.project.domain.repo;

import com.project.project.domain.entity.ProjectMember;
import com.project.project.domain.entity.ProjectRole;
import com.project.project.domain.entity.project.Project;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProjectMemberJpaRepository extends JpaRepository<ProjectMember, Long> {
    List<ProjectMember> findAllByUser_Username(String username);
    boolean existsByProjectAndUserUsername(Project project, String username);
    Optional<ProjectMember> findByProject_IdAndUser_IdAndRole(Long projectId, Long userId, ProjectRole projectRole);
    Optional<ProjectMember> findByProject_IdAndUser_Id(Long projectId, Long userId);
    Optional<ProjectMember> findByProject_IdAndUser_Username(Long projectId, String username);
    List<ProjectMember> findAllByUser_Id(Long userId);
}
