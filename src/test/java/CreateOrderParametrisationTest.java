import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class CreateOrderParametrisationTest {
    private static final String BASE_URI = "http://qa-scooter.praktikum-services.ru/";
    public static final String CREATE_ORDER_PATH = "/api/v1/orders";

    private final String colorList;
    private final int expected;

    public CreateOrderParametrisationTest(String colorList, int expected) {
        this.colorList = colorList;
        this.expected = expected;
    }

    @Parameterized.Parameters
    public static Object[] getSumData() {
        return new Object[][] {
                { "src/test/resources/order1.json", 201},
                { "src/test/resources/order2.json", 201},
                { "src/test/resources/order3.json", 201},
                { "src/test/resources/order4.json", 201},
        };
    }

    //можно указать один из цветов — BLACK или GREY;
    //можно указать оба цвета;
    //можно совсем не указывать цвет
    @Test
    public void checkStatusCode201() {
        RestAssured.baseURI = BASE_URI;
        File json = new File(colorList);
        ValidatableResponse response = given()
                .header("Content-type", "application/json")
                .body(json)
                .post(CREATE_ORDER_PATH)
                .then();
        assertEquals(expected, response.extract().statusCode());
    }

    // тело ответа содержит track
    @Test
    public void checkCorrectBody() {
        RestAssured.baseURI = BASE_URI;
        File json = new File(colorList);
        ValidatableResponse response = given()
                .header("Content-type", "application/json")
                .body(json)
                .post(CREATE_ORDER_PATH)
                .then();
        assertNotNull(response.extract().path("track"));
    }
}