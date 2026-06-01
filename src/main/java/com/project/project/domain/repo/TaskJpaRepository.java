package com.project.project.domain.repo;

import com.project.project.domain.entity.Task;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TaskJpaRepository extends JpaRepository<Task, Long> {

    List<Task> findAllByProject_Id(Long projectId);
}
