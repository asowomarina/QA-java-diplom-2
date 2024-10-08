package ru.yandex.praktikum.user;

import org.junit.After;
import org.junit.Test;
import org.junit.Before;
import io.qameta.allure.Epic;
import io.qameta.allure.junit4.DisplayName;
import ru.yandex.praktikum.entity.User;
import ru.yandex.praktikum.api.UserClient;
import ru.yandex.praktikum.utils.GenerateUser;
import org.apache.commons.lang3.StringUtils;
import io.restassured.response.ValidatableResponse;
import static org.apache.http.HttpStatus.SC_OK;
import static org.apache.http.HttpStatus.SC_FORBIDDEN;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;

@Epic("Create user")
public class UserCreateTest {
    private static final String MESSAGE_FORBIDDEN = "User already exists";
    private static final String MESSAGE_FORBIDDEN_EMPTY_FIELD = "Email, password and name are required fields";
    private ValidatableResponse response;
    private UserClient userClient;
    private User user;

    @Before
    public void setUp() {
        user = GenerateUser.getRandomUser();
        userClient = new UserClient();
    }

    @After
    public void tearDown() {
        userClient.deleteAllUsers();
    }

    @Test
    @DisplayName("User create by valid credentials")
    public void userCreateByValidCredentials() {
        response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        boolean isCreate = response.extract().path("success");
        String accessToken = response.extract().path("accessToken");
        response = userClient.deleteUser(StringUtils.substringAfter(accessToken, " "));

        assertThat("Code not equal", statusCode, equalTo(SC_OK));
        assertThat("User is create incorrect", isCreate, equalTo(true));
    }

    @Test
    @DisplayName("User create is empty email")
    public void userCreateIsEmptyEmail() {
        user.setEmail(null);
        response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreate = response.extract().path("success");

        assertThat("Code not equal", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Message not equal", message, equalTo(MESSAGE_FORBIDDEN_EMPTY_FIELD));
        assertThat("User is create correct", isCreate, equalTo(false));
    }

    @Test
    @DisplayName("User create is empty password")
    public void userCreateIsEmptyPassword() {
        user.setPassword(null);
        response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreate = response.extract().path("success");

        assertThat("Code not equal", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Message not equal", message, equalTo(MESSAGE_FORBIDDEN_EMPTY_FIELD));
        assertThat("User is create correct", isCreate, equalTo(false));
    }

    @Test
    @DisplayName("User create is empty name")
    public void userCreateIsEmptyName() {
        user.setName(null);
        response = userClient.createUser(user);
        int statusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreate = response.extract().path("success");

        assertThat("Code not equal", statusCode, equalTo(SC_FORBIDDEN));
        assertThat("Message not equal", message, equalTo(MESSAGE_FORBIDDEN_EMPTY_FIELD));
        assertThat("User is create correct", isCreate, equalTo(false));
    }

    @Test
    @DisplayName("Repeated request by create user")
    public void repeatedRequestByCreateUser() {
        // Создание пользователя
        response = userClient.createUser(user);
        response.extract().statusCode();
        String accessToken = response.extract().path("accessToken");

        // Повторное создание пользователя
        response = userClient.createUser(user);
        int repeatedStatusCode = response.extract().statusCode();
        String message = response.extract().path("message");
        boolean isCreate = response.extract().path("success");

        // Удаление пользователя
        userClient.deleteUser(accessToken);

        assertThat("Code not equal", repeatedStatusCode, equalTo(SC_FORBIDDEN));
        assertThat("Message not equal", message, equalTo(MESSAGE_FORBIDDEN));
        assertThat("User is create correct", isCreate, equalTo(false));
    }

}