import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

public class LoginOfUserTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Log in as an user")
    public void userLoginSuccessfully() {
        User user = Steps.createUser("test-data" + new Random().nextInt(1000) + "@yandex.ru", "password", "Username");
        Steps.postForCreationUser(user).then().statusCode(200);

        Login login = Steps.createLogin(user.getEmail(), user.getPassword());
        Response responseLogin = Steps.login(login);
        responseLogin.then().assertThat().body("accessToken", notNullValue())
                .and()
                .statusCode(200);

        Steps.deleteUser(Steps.getAccessToken(responseLogin), user);
    }

    @Test
    @DisplayName("Check login as an user with incorrect pair email-password")
    public void notPossibleToLoginWithIncorrectPairEmailPassword() {
        User user = Steps.createUser("test-data" + new Random().nextInt(1000) + "@yandex.ru", "password", "Username");
        Response responseUser = Steps.postForCreationUser(user);
        responseUser.then().statusCode(200);

        Login login = Steps.createLogin(user.getEmail() + "1", user.getPassword() + "1");
        Steps.login(login).then().assertThat().body("message", equalTo("email or password are incorrect"))
                .and()
                .statusCode(401);

        Steps.deleteUser(Steps.getAccessToken(responseUser), user);
    }

}
