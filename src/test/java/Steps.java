import io.qameta.allure.Step;
import io.restassured.response.Response;

import static io.restassured.RestAssured.given;

public final class Steps {
    @Step("Create a user")
    public static User createUser(String email, String password, String name) {
        return new User(email, password, name);
    }

    @Step("Create a login object")
    public static Login createLogin(String email, String password) {
        return new Login(email, password);
    }

    @Step("POST for creation an user")
    public static Response postForCreationUser(User user) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(user)
                .when()
                .post("/api/auth/register");
    }

    @Step("Login")
    public static Response login(Login login) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(login)
                .when()
                .post("/api/auth/login");
    }

    @Step("Create an order")
    public static Order createOrder(String[] ingredients) {
        return new Order(ingredients);
    }

    @Step("POST for creation an order")
    public static Response postForCreationOrder(Order order) {
        return given()
                .header("Content-type", "application/json")
                .and()
                .body(order)
                .when()
                .post("/api/orders");
    }

    @Step("Get number of order")
    public static String getNumber(Response response) {
        return response.then().extract().body().path("number").toString();
    }

    @Step("List of orders")
    public static Response listOfOrders(String accessToken) {
        return given().header("Authorization", accessToken).get("/api/orders");
    }

    @Step("Delete an user")
    public static void deleteUser(String accessToken, User user) {
        given().header("Authorization", accessToken)
                .and()
                .body(user)
                .when()
                .delete("/api/auth/user")
                .then()
                .statusCode(202);
    }

    @Step("Get accessToken from response")
    public static String getAccessToken(Response response) {
        return response.then().extract().body().path("accessToken");
    }

    @Step("Update user")
    public static Response updateUser(User user, String accessToken) {
        if (accessToken != null) {
            return given().header("Authorization", accessToken)
                    .and()
                    .body(user)
                    .when()
                    .patch("/api/auth/user");
        }
        return given()
                .body(user)
                .when()
                .patch("/api/auth/user");
    }

}
