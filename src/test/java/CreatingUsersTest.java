import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.hamcrest.Matchers.*;

public class CreatingUsersTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Check creating an user")
    public void creatingUserSuccessfully() {
        User user = Steps.createUser("test-data" + new Random().nextInt(1000) + "@yandex.ru", "password", "Username");
        Response response = Steps.postForCreationUser(user);
        response.then().assertThat().body("success", equalTo(true))
                .and()
                .statusCode(200);
        Steps.deleteUser(Steps.getAccessToken(response), user);
    }

    @Test
    @DisplayName("Check creating two identical users")
    public void notPossibleToCreateTwoIdenticalUsers() {
        User user = Steps.createUser("test-data" + new Random().nextInt(1000) + "@yandex.ru", "password", "Username");
        Response responseFirstUser = Steps.postForCreationUser(user);
        Steps.postForCreationUser(user).then().assertThat().body("success", equalTo(false))
                .and()
                .body("message", equalTo("User already exists"))
                .and()
                .statusCode(403);
        Steps.deleteUser(Steps.getAccessToken(responseFirstUser), user);
    }

    @Test
    @DisplayName("Check validation of required email")
    public void notPossibleToCreateUserWithoutEmail() {
        User userWithoutEmail = Steps.createUser("", "password", "Username");
        Response response = Steps.postForCreationUser(userWithoutEmail);
        response.then().assertThat().body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);
    }

    @Test
    @DisplayName("Check validation of required password")
    public void notPossibleToCreateUserWithoutPassword() {
        User userWithoutEmail = Steps.createUser("test-data" + new Random().nextInt(1000) + "@yandex.ru", "", "Username");
        Response response = Steps.postForCreationUser(userWithoutEmail);
        response.then().assertThat().body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);
    }

    @Test
    @DisplayName("Check validation of required name")
    public void notPossibleToCreateUserWithoutName() {
        User userWithoutEmail = Steps.createUser("test-data" + new Random().nextInt(1000) + "@yandex.ru", "password", "");
        Response response = Steps.postForCreationUser(userWithoutEmail);
        response.then().assertThat().body("success", equalTo(false))
                .and()
                .body("message", equalTo("Email, password and name are required fields"))
                .and()
                .statusCode(403);
    }
}
