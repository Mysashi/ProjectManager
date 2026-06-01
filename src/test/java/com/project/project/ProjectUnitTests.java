package com.project.project;

import com.project.project.domain.dto.request.create.CreateProjectRequestDto;
import com.project.project.domain.dto.response.ProjectResponseDto;
import com.project.project.domain.entity.ProjectMember;
import com.project.project.domain.entity.project.ProjectStatus;
import com.project.project.domain.impl.ProjectServiceImpl;
import com.project.project.domain.repo.ProjectJpaRepository;
import com.project.project.domain.repo.ProjectMemberJpaRepository;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.security.test.context.support.WithMockUser;

import java.time.LocalDateTime;
import java.util.List;

@SpringBootTest
@WithMockUser(username = "user")
public class ProjectUnitTests {

    private final ProjectServiceImpl projectService;
    private final ProjectJpaRepository projectJpaRepository;
    private final ProjectMemberJpaRepository projectMemberJpaRepository;

    @Autowired
    public ProjectUnitTests(ProjectServiceImpl projectService, ProjectJpaRepository projectJpaRepository, ProjectMemberJpaRepository projectMemberJpaRepository) {
        this.projectService = projectService;
        this.projectJpaRepository = projectJpaRepository;
        this.projectMemberJpaRepository = projectMemberJpaRepository;
    }

    @Test
    void shouldCreateProjectSuccess() {
        var nameOfProject = "projectUnitTest";
        var description = "...";
        ProjectResponseDto responseDto = createProject(nameOfProject, description);
        var found = projectJpaRepository.findById(responseDto.id());
        Assertions.assertNotNull(found);
        Assertions.assertEquals(found.get().getId(), responseDto.id());
    }


    @Test
    void shouldArchiveProjectSuccess() {
        var nameOfProject = "projectArchiveUnitTest";
        var description = "...";
        ProjectResponseDto responseDto = createProject(nameOfProject, description);
        var response = projectService.archive(responseDto.id(), 10L);
        LocalDateTime expectedDate = LocalDateTime.now().plusDays(10L);


        Assertions.assertNotNull(response);
        Assertions.assertEquals(ProjectStatus.ARCHIVED.toString(), response.status());
        Assertions.assertTrue(
                response.dateOfDeletion().isAfter(expectedDate.minusSeconds(5)) &&
                        response.dateOfDeletion().isBefore(expectedDate.plusSeconds(5))
        );
    }

    @Test
    void shouldAddParticipantIfOwnerSuccess() {
        var projectResponse = createProject("projectAddParticipantTest", "...");
        var response = projectService.addParticipantToProject(2L, projectResponse.id());
        List<ProjectMember> projectMemberList = projectMemberJpaRepository.findAllByUser_Id(2L);
        boolean isParticipantAdded = projectMemberList.stream()
                .anyMatch(member -> member.getUser().getId().equals(2L));
        Assertions.assertTrue(isParticipantAdded);
        Assertions.assertEquals("projectAddParticipantTest", response.name());
    }

    @Test
    void shouldDeleteParticipantIfOwnerSuccess() {
        var projectResponse = createProject("projectDeleteParticipantTest", "...");
        projectService.joinProject(projectResponse.id(), "user2");
        var response = projectService.deleteParticipantOfProject(2L, projectResponse.id());
        List<ProjectMember> projectMemberList = projectMemberJpaRepository.findAllByUser_Id(2L);
        boolean isParticipantInProject = projectMemberList.stream()
                .anyMatch(member -> member.getUser().getId().equals(2L));
        Assertions.assertFalse(isParticipantInProject);
        Assertions.assertEquals("projectDeleteParticipantTest", response.name());
    }

//    @Test
//    void shouldGetTasksOfProjectSuccess() {
//        var projectResponse = createProject("projectGetTasksTest", "...");
//        List<TaskResponseDto> listOfTasks = projectService.getTasksOfProject(projectResponse.id());
//        Assertions.assertNotNull(listOfTasks);
//    }

    private ProjectResponseDto createProject(String nameOfProject, String description) {
        var request = new CreateProjectRequestDto(nameOfProject, description);
        var response = projectService.create(request);
        return response;
    }


}
