import io.qameta.allure.junit4.DisplayName;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.Before;
import org.junit.Test;

import java.util.Random;

import static org.hamcrest.Matchers.*;

public class GetOrdersForUserTest {

    @Before
    public void setUp() {
        RestAssured.baseURI = "https://stellarburgers.nomoreparties.site/";
    }

    @Test
    @DisplayName("Get orders for authorized user")
    public void getOrdersForAuthorizedUser() {
        User user = Steps.createUser("test-data" + new Random().nextInt(10000) + "@yandex.ru", "password", "Username");
        Response response = Steps.postForCreationUser(user);
        response.then().statusCode(200);
        String accessToken = Steps.getAccessToken(response);
        Login login = Steps.createLogin(user.getEmail(), user.getPassword());
        Steps.login(login).then().statusCode(200);

        String[] ingredients = new String[]{"61c0c5a71d1f82001bdaaa70", "61c0c5a71d1f82001bdaaa6d"};

        Order order = Steps.createOrder(ingredients);
        Steps.postForCreationOrder(order).then().statusCode(200);
        Steps.listOfOrders(accessToken)
                .then()
                .body("total", notNullValue())
                .and()
                .statusCode(200);

        Steps.deleteUser(Steps.getAccessToken(response), user);
    }

    @Test
    @DisplayName("Get orders for an unauthorized user")
    public void getOrdersForUnauthorizedUser() {
        {
            Steps.listOfOrders("")
                    .then().body("message", equalTo("You should be authorised"))
                    .and()
                    .statusCode(401);
        }
    }
}
