import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.util.Random;

@RunWith(Parameterized.class)
public class CreatingOrderTest {
    private final String[] ingredients;
    private final int expected;

    public CreatingOrderTest(String[] ingredients, int expected) {
        this.ingredients = ingredients;
        this.expected = expected;
    }

    private static User user = new User("test-data" + new Random().nextInt(10000) + "@yandex.ru", "password", "Username");
    private String accessToken;

    @Parameterized.Parameters(name = "Test Data {0}, {1}")
    public static Object[] getIngredientCode() {
        return new Object[][]{
                {new String[]{"61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa6d"}, 200},
                {new String[]{}, 400},
                {new String[]{"blabla"}, 500}
        };
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

    @Test
    @DisplayName("Check creating orders for authorized user")
    public void orderCreatingForAuthorizedUser() {
        Login login = new Login(user.getEmail(), user.getPassword());
        Steps.login(login).then().statusCode(200);
        Order order = Steps.createOrder(ingredients);
        Response response = Steps.postForCreationOrder(order);
        response.then().statusCode(expected);
    }

    @Test
    @DisplayName("Check creating an order for non-authorized user")
    public void notPossibleToCreateOrderForNonAuthorizedUser() {
        Order order = Steps.createOrder(ingredients);
        Response response = Steps.postForCreationOrder(order);
        response.then().statusCode(expected);
    }
}
