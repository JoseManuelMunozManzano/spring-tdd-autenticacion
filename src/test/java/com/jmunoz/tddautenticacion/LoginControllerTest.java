package com.jmunoz.tddautenticacion;

import com.jmunoz.tddautenticacion.error.ApiError;
import com.jmunoz.tddautenticacion.user.User;
import com.jmunoz.tddautenticacion.user.UserRepository;
import com.jmunoz.tddautenticacion.user.UserService;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.client.support.BasicAuthenticationInterceptor;
import org.springframework.test.context.ActiveProfiles;

import java.util.Map;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class LoginControllerTest {

    public static final String API_1_0_LOGIN = "/api/1.0/login";

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserRepository userRepository;

    @Autowired
    UserService userService;

    public <T> ResponseEntity<T> login(Class<T> responseType) {
        return testRestTemplate.postForEntity(API_1_0_LOGIN, null, responseType);
    }

    public <T> ResponseEntity<T> login(ParameterizedTypeReference<T> responseType) {
        return testRestTemplate.exchange(API_1_0_LOGIN, HttpMethod.POST, null, responseType);
    }

    private boolean authenticate() {
        return testRestTemplate
                .getRestTemplate().getInterceptors()
                .add(new BasicAuthenticationInterceptor("test-user", "P4ssword"));
    }

    @AfterEach
    void tearDown() {
        userRepository.deleteAll();
        testRestTemplate.getRestTemplate().getInterceptors().clear();
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

        assertThat(response.getBody().getUrl()).isEqualTo(API_1_0_LOGIN);
    }

    @Test
    void postLogin_withoutUserCredentials_receiveApiErrorWithoutValidationErrors() {
        ResponseEntity<String> response = login(String.class);
        assertThat(response.getBody().contains("validationErrors")).isFalse();
    }

    @Test
    void postLogin_withIncorrectCredentials_receiveUnauthorizedWithoutWWWAuthenticationHeader() {
        authenticate();

        ResponseEntity<Object> response = login(Object.class);

        assertThat(response.getHeaders().containsKey("WWW-Authenticate")).isFalse();
    }

    @Test
    void postLogin_withValidCredentials_receiveOk() {
        userService.save(TestUtil.createValidUser());
        authenticate();

        ResponseEntity<Object> response = login(Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void postLogin_withValidCredentials_receiveLoggedInUserId() {
        User inDb = userService.save(TestUtil.createValidUser());
        authenticate();

        ResponseEntity<Map<String, Object>> response =
                login(new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String, Object> body = response.getBody();
        Integer id = (Integer) body.get("id");

        assertThat(id).isEqualTo(inDb.getId());
    }

    @Test
    void postLogin_withValidCredentials_receiveLoggedInUsersImage() {
        User inDb = userService.save(TestUtil.createValidUser());
        authenticate();

        ResponseEntity<Map<String, Object>> response =
                login(new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String, Object> body = response.getBody();

        String image = (String) body.get("image");

        assertThat(image).isEqualTo(inDb.getImage());
    }

    @Test
    void postLogin_withValidCredentials_receiveLoggedInUsersDisplayName() {
        User inDb = userService.save(TestUtil.createValidUser());
        authenticate();

        ResponseEntity<Map<String, Object>> response =
                login(new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String, Object> body = response.getBody();

        String displayName = (String) body.get("displayName");

        assertThat(displayName).isEqualTo(inDb.getDisplayName());
    }

    @Test
    void postLogin_withValidCredentials_receiveLoggedInUsersUsername() {
        User inDb = userService.save(TestUtil.createValidUser());
        authenticate();

        ResponseEntity<Map<String, Object>> response =
                login(new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String, Object> body = response.getBody();

        String username = (String) body.get("username");

        assertThat(username).isEqualTo(inDb.getUsername());
    }

    // IMPORTANTE!!! No debemos recibir el password
    @Test
    void postLogin_withValidCredentials_notReceiveLoggedInPassword() {
        User inDb = userService.save(TestUtil.createValidUser());
        authenticate();

        ResponseEntity<Map<String, Object>> response =
                login(new ParameterizedTypeReference<Map<String, Object>>() {});

        Map<String, Object> body = response.getBody();

        assertThat(body.containsKey("password")).isFalse();
    }
}
