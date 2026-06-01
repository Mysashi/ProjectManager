package com.project.project.tests.restAssured;

import com.project.project.domain.entity.User;
import com.project.project.domain.repo.UserJpaRepository;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static io.restassured.RestAssured.given;

@SpringBootTest
public class AuthRestTests {

    private final UserJpaRepository userJpaRepository;

    @Autowired
    public AuthRestTests(UserJpaRepository userJpaRepository) {
        this.userJpaRepository = userJpaRepository;
    }

    @BeforeEach
    public void setup() {
        User user = new User();
        user.setUsername("user");
        user.setPassword("user");
        RestAssured.baseURI = "http://localhost";
        RestAssured.port = 8080;
    }

    @Test
    void shouldAuthUserSuccessAndReturn200() {
        given().auth()
                .preemptive()
                .basic("user", "user")
                .then().statusCode(200);

    }

    @Test
    void shouldAuthUserFailAndReturn401() {
        given().auth()
                .preemptive()
                .basic("notFound", "notFound")
                .then().statusCode(401);

    }

}
