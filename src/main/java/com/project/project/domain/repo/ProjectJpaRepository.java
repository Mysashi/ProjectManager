package com.project.project.domain.repo;

import com.project.project.domain.entity.project.Project;
import com.project.project.domain.entity.project.ProjectStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;


public interface ProjectJpaRepository extends JpaRepository<Project, Long> {
    boolean existsByName(String name);

    @Modifying
    @Query("UPDATE Project p SET p.status = :status WHERE p.id = :id")
    void updateStatus(@Param("id") Long id, @Param("status") ProjectStatus status);
}
