package com.project.project.domain.impl;

import com.project.project.domain.repo.ProjectJpaRepository;
import com.project.project.domain.service.CleanupService;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
@EnableScheduling
public class CleanupServiceImpl implements CleanupService {
    private final ProjectJpaRepository projectRepository;

    public CleanupServiceImpl(ProjectJpaRepository projectRepository) {
        this.projectRepository = projectRepository;
    }

    @Scheduled(cron = "0 0 0 * * *")
    public void removeOldProjects() {
        projectRepository.deleteExpiredProjects(LocalDateTime.now());
    }
}
