import io.restassured.RestAssured;
import io.restassured.response.ValidatableResponse;

import java.util.Random;

import static io.restassured.RestAssured.given;

public class ClientCourier {
    private static final String BASE_URI = "http://qa-scooter.praktikum-services.ru/";
    public static final String CREATE_PATH = "/api/v1/courier";
    public static final String LOGIN_PATH = "/api/v1/courier/login";
    private static final String DELETE_PATH = "/api/v1/courier/:id";
    public ClientCourier() {RestAssured.baseURI = BASE_URI;}

    public static String randomString(int length) {
        Random random = new Random();
        int leftLimit = 97;
        int rightLimit = 122;
        StringBuilder buffer = new StringBuilder(length);

        for(int i = 0; i < length; i++) {
            int randomLimitedInt = leftLimit + (int)(random.nextFloat() * (float)(rightLimit - leftLimit + 1));
            buffer.append(Character.toChars(randomLimitedInt));
        }
        return buffer.toString();
    }

    public static CreateCourier randomCourier() {
        return new CreateCourier()
                .setLogin(randomString(7))
                .setPassword(randomString(7))
                .setFirstName(randomString(7));
    }

    public ValidatableResponse create(CreateCourier courier) {
        return given()
                .header("Content-type", "application/json")
                .body(courier)
                .post(CREATE_PATH)
                .then();
    }

    public ValidatableResponse login(LoginCourier loginCourier) {
        return given()
                .header("Content-type", "application/json")
                .body(loginCourier)
                .post(LOGIN_PATH)
                .then();
    }

    public ValidatableResponse delete(DeleteCourier deleteCourier) {
        return given()
                .header("Content-type", "application/json")
                .body(deleteCourier)
                .delete(DELETE_PATH)
                .then();
    }
}
