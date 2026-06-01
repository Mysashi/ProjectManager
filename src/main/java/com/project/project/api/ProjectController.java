package com.project.project.api;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

@Slf4j
@Controller
public class ProjectController {

    @GetMapping("/project-page")
    public String showProjectPage(@RequestParam Long id, Authentication auth) {
        log.info(String.valueOf(auth.isAuthenticated()));
        if (auth == null || !auth.isAuthenticated()) return "redirect:/auth/login";
        return "project";
    }
}
