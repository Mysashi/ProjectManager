package com.project.project;

import com.project.project.domain.dto.request.create.CreateProjectRequestDto;
import com.project.project.domain.dto.request.update.TransferProjectRequestDto;
import com.project.project.domain.dto.response.ProjectResponseDto;
import com.project.project.domain.entity.ProjectRole;
import com.project.project.domain.impl.ProjectMemberServiceImpl;
import com.project.project.domain.impl.ProjectServiceImpl;
import com.project.project.domain.repo.ProjectMemberJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

@SpringBootTest
@WithMockUser(username = "user")
public class ProjectMemberUnitTests {

    private final ProjectMemberServiceImpl projectMemberService;

    private final ProjectMemberJpaRepository projectMemberJpaRepository;

    @Autowired
    private ProjectServiceImpl projectService;

    @Autowired
    public ProjectMemberUnitTests(ProjectMemberServiceImpl projectMemberService,
                                  ProjectMemberJpaRepository projectMemberJpaRepository) {
        this.projectMemberService = projectMemberService;
        this.projectMemberJpaRepository = projectMemberJpaRepository;
    }

    @Test
    void shouldFindProjectsByUsernameSuccess() {
        var createProjectResponse = createProject("projectFindProjectsByNameTest", "...");
        var found = projectMemberService.findProjectsByUsername("user");
        Assertions.assertNotNull(found);
    }

    private ProjectResponseDto createProject(String nameOfProject, String description) {
        var request = new CreateProjectRequestDto(nameOfProject, description);
        return projectService.create(request);
    }


}
