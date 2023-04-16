import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GetOrderListTest {
    CreateCourier createCourier;
    ClientCourier client;
    LoginCourier loginCourier;
    DeleteCourier deleteCourier;
    public static final String CREATE_ORDER_PATH = "/api/v1/orders";

    @Before
    public void setUp() {
        createCourier = ClientCourier.randomCourier();
        client = new ClientCourier();
        loginCourier = new LoginCourier(createCourier.getLogin(), createCourier.getPassword());
    }

    @After
    public void tearDown() {
        ValidatableResponse response = client.login(loginCourier);
        int num = response.extract().path("id");
        deleteCourier = new DeleteCourier(num);
        client.delete(deleteCourier);
    }

    @Test
    public void checkOrderListNotNull() {
        File json = new File("src/test/resources/order1.json");
        ValidatableResponse response = client.create(createCourier);
        assertEquals("Клиент не создан", 201, response.extract().statusCode());
        assertEquals("не возвращает ok: true", true, response.extract().path("ok"));
        ValidatableResponse response1 = client.login(loginCourier);
        assertEquals(200, response1.extract().statusCode());
        assertNotNull(response1.extract().path("id"));
        int courierId = response1.extract().path("id");
        ValidatableResponse response2 = given()
                .header("Content-type", "application/json")
                .body(json)
                .post(CREATE_ORDER_PATH)
                .then();
        assertEquals(201, response2.extract().statusCode());
        assertNotNull(response2.extract().path("track"));
        int orderTrack = response2.extract().path("track");
        ValidatableResponse response3 = given()
                .header("Content-type", "application/json")
                .get("/api/v1/orders/track?t=" + orderTrack)
                .then();
        assertEquals(200, response3.extract().statusCode());
        assertNotNull(response3.extract().path("order.id"));
        ValidatableResponse response4 = given()
                .header("Content-type", "application/json")
                .get("/api/v1/orders?courierId=" + courierId)
                .then();
        assertEquals(200, response4.extract().statusCode());
        assertNotNull(response3.extract().path("order"));
    }
}