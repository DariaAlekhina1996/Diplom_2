import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Random;

import static org.hamcrest.Matchers.equalTo;

@RunWith(Parameterized.class)
public class UpdateUserInfoTest {
    private final String userName;
    private final String userEmail;
    private final String userPassword;

    private static User user = new User("test-data" + new Random().nextInt(10000) + "@yandex.ru", "password", "Username");
    private String accessToken;

    public UpdateUserInfoTest(String userName, String userEmail, String userPassword) {
        this.userName = userName;
        this.userEmail = userEmail;
        this.userPassword = userPassword;
    }

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
        Response responseUser = Steps.postForCreationUser(user);
        responseUser.then().statusCode(200);
        accessToken = Steps.getAccessToken(responseUser);
    }

    @After
    public void removeUser() {
        Steps.deleteUser(accessToken, user);
    }

    @Parameterized.Parameters(name = "Test data: {0} {1} {2}")
    public static Object[] getData() {
        return new Object[][]{
                {user.getName(), "email1@yandex.ru", "password1"},
                {"blabla", "email2@yandex.ru", user.getPassword()},
                {"blabla1", user.getEmail(), user.getPassword()},
                {"blabla2", user.getEmail(), "password2"},
                {user.getName(), user.getEmail(), "password3"},
                {user.getName(), "email3@yandex.ru", user.getPassword()}
        };
    }

    @Test
    @DisplayName("Check to update the user information for authorized user")
    public void updatingUserInfoSuccessfully() {
        User updatedUser = Steps.createUser(userName, userEmail, userPassword);
        Response response = Steps.updateUser(updatedUser, accessToken);
        response.then().body("success", equalTo(true))
                .and()
                .statusCode(200);
    }

    @Test
    @DisplayName("Check to update the user information for non-authorized user")
    public void notPossibleToUpdateUserInfoWithoutAuthorization() {
        User updatedUser = Steps.createUser(userName, userEmail, userPassword);
        Response response = Steps.updateUser(updatedUser, null);
        response.then().body("message", equalTo("You should be authorised"))
                .and()
                .statusCode(401);
    }
}
