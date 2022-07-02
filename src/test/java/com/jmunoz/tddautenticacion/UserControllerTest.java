package com.jmunoz.tddautenticacion;

import com.jmunoz.tddautenticacion.error.ApiError;
import com.jmunoz.tddautenticacion.shared.GenericResponse;
import com.jmunoz.tddautenticacion.user.User;
import com.jmunoz.tddautenticacion.user.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.assertj.core.api.Assertions.*;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ActiveProfiles("test")
public class UserControllerTest {

    public static final String API_1_0_USERS = "/api/1.0/users";

    @Autowired
    TestRestTemplate testRestTemplate;

    @Autowired
    UserRepository userRepository;

    private User createValidUser() {
        User user = new User();
        user.setUsername("test-user");
        user.setDisplayName("test-display");
        user.setPassword("P4ssword");

        return user;
    }

    public <T> ResponseEntity<T> postSignUp(Object request, Class<T> response) {
        return testRestTemplate.postForEntity(API_1_0_USERS, request, response);
    }

    @BeforeEach
    void setUp() {
        userRepository.deleteAll();
    }

    @Test
    void postUser_whenUserIsValid_receiveOk() {
        User user = createValidUser();

        // Se pasa url, request, response y (en este caso no) variables de url
        ResponseEntity<Object> response = postSignUp(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.OK);
    }

    @Test
    void postUser_whenUserIsValid_userSavedToDatabase() {
        User user = createValidUser();

        postSignUp(user, Object.class);

        assertThat(userRepository.count()).isEqualTo(1);
    }

    @Test
    void postUser_whenUserIsValid_receiveSuccessMessage() {
        User user = createValidUser();

        ResponseEntity<GenericResponse> response = postSignUp(user, GenericResponse.class);

        assertThat(response.getBody().getMessage()).isNotNull();
    }

    @Test
    void postUser_whenUserIsValid_passwordIsHashedInDatabase() {
        User user = createValidUser();

        postSignUp(user, Object.class);

        List<User> users = userRepository.findAll();
        User inDb = users.get(0);

        assertThat(inDb.getPassword()).isNotEqualTo(user.getPassword());
    }

    @Test
    void postUser_whenUserHasNullUsername_receiveBadRequest() {
        User user = createValidUser();
        user.setUsername(null);

        ResponseEntity<Object> response = postSignUp(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasNullDisplayName_receiveBadRequest() {
        User user = createValidUser();
        user.setDisplayName(null);

        ResponseEntity<Object> response = postSignUp(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasNullPassword_receiveBadRequest() {
        User user = createValidUser();
        user.setPassword(null);

        ResponseEntity<Object> response = postSignUp(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasUsernameWithLessThanRequired_receiveBadRequest() {
        User user = createValidUser();
        // mínimo 4 caracteres
        user.setUsername("abc");

        ResponseEntity<Object> response = postSignUp(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasDisplayNameWithLessThanRequired_receivedBadRequest() {
        User user = createValidUser();
        // mínimo 4 caracteres
        user.setDisplayName("abc");

        ResponseEntity<Object> response = postSignUp(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasPasswordWithLessThenRequired_receiveBadRequest() {
        User user = createValidUser();
        // mínimo 8 caracteres, con mayúsculas, minúsculas y números
        user.setPassword("P4ssd");

        ResponseEntity<Object> response = postSignUp(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasUsernameThanExceedsTheLengthLimit_receiveBadRequest() {
        User user = createValidUser();
        // máximo 255 caracteres
        String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setUsername(valueOf256Chars);

        ResponseEntity<Object> response = postSignUp(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasDisplayNameThanExceedsTheLengthLimit_receiveBadRequest() {
        User user = createValidUser();
        // máximo 255 caracteres
        String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setDisplayName(valueOf256Chars);

        ResponseEntity<Object> response = postSignUp(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasPasswordThanExceedsTheLengthLimit_receiveBadRequest() {
        User user = createValidUser();
        // máximo 255 caracteres
        String valueOf256Chars = IntStream.rangeClosed(1, 256).mapToObj(x -> "a").collect(Collectors.joining());
        user.setPassword(valueOf256Chars + "A1");

        ResponseEntity<Object> response = postSignUp(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasPasswordWithAllLowercase_receiveBadRequest() {
        User user = createValidUser();

        user.setPassword("alllowercase");

        ResponseEntity<Object> response = postSignUp(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasPasswordWithAllUppercase_receiveBadRequest() {
        User user = createValidUser();

        user.setPassword("ALLUPPERCASE");

        ResponseEntity<Object> response = postSignUp(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserHasPasswordWithAllNumbers_receiveBadRequest() {
        User user = createValidUser();

        user.setPassword("1234567890");

        ResponseEntity<Object> response = postSignUp(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenUserIsInvalid_receiveApiError() {
        User user = new User();

        ResponseEntity<ApiError> response = postSignUp(user, ApiError.class);

        assertThat(response.getBody().getUrl()).isEqualTo(API_1_0_USERS);
    }

    @Test
    void postUser_whenUserIsInvalid_receiveApiErrorWithValidationErrors() {
        User user = new User();

        ResponseEntity<ApiError> response = postSignUp(user, ApiError.class);

        assertThat(response.getBody().getValidationErrors().size()).isEqualTo(3);
    }

    @Test
    void postUser_whenUserHasNullUsername_receiveMessageOfNullErrorForUsername() {
        User user = createValidUser();
        user.setUsername(null);

        ResponseEntity<ApiError> response = postSignUp(user, ApiError.class);

        Map<String, String> validationErrors = response.getBody().getValidationErrors();

        assertThat(validationErrors.get("username")).isEqualTo("Username cannot be null");
    }

    @Test
    void postUser_whenUserHanInvalidLengthUsername_receiveGenericMessageOfSizeError() {
        User user = createValidUser();
        user.setUsername("abc");

        ResponseEntity<ApiError> response = postSignUp(user, ApiError.class);

        Map<String, String> validationErrors = response.getBody().getValidationErrors();

        assertThat(validationErrors.get("username"))
                .isEqualTo("It must have minimum 4 and maximum 255 characters");
    }

    @Test
    void postUser_whenUserHasInvalidPassword_receiveMessageOfPasswordPatternError() {
        User user = createValidUser();
        user.setPassword("alllowercase");

        ResponseEntity<ApiError> response = postSignUp(user, ApiError.class);

        Map<String, String> validationErrors = response.getBody().getValidationErrors();

        assertThat(validationErrors.get("password"))
                .isEqualTo("Password must have at least one uppercase, one lowercase letter and one number");
    }

    @Test
    void postUser_whenAnotherUserHasSameUsername_receiveBadRequest() {
        userRepository.save(createValidUser());

        User user = createValidUser();

        ResponseEntity<Object> response = postSignUp(user, Object.class);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
    }

    @Test
    void postUser_whenAnotherUserHasSameUsername_receiveMessageofDuplicateUsername() {
        userRepository.save(createValidUser());

        User user = createValidUser();

        ResponseEntity<ApiError> response = postSignUp(user, ApiError.class);

        Map<String, String> validationErrors = response.getBody().getValidationErrors();

        assertThat(validationErrors.get("username")).isEqualTo("This name is in use");
    }
}
