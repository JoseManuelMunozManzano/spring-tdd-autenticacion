package com.jmunoz.tddautenticacion;

import com.jmunoz.tddautenticacion.error.ApiError;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoginControllerTest {

    public static final String API_1_0_USERS = "/api/1.0/login";

    @Autowired
    TestRestTemplate testRestTemplate;

    public <T>ResponseEntity<T> login(Class<T> responseType) {
        return testRestTemplate.postForEntity(API_1_0_USERS, null, responseType);
    }

    private boolean authenticate() {
        return testRestTemplate
                .getRestTemplate().getInterceptors()
                .add(new BasicAuthenticationInterceptor("test-user", "P4ssword"));
    }

    @Test
    void postLogin_withoutUserCredentials_receiveUnauthorized() {
        ResponseEntity<Object> response = login(Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void postLogin_withIncorrectCredentials_receiveUnauthorized() {
        authenticate();

        ResponseEntity<Object> response = login(Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
    }

    @Test
    void postLogin_withoutUserCredentials_receiveApiError() {
        ResponseEntity<ApiError> response = login(ApiError.class);

        assertThat(response.getBody().getUrl()).isEqualTo(API_1_0_USERS);
    }
}
