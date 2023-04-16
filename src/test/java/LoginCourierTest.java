import io.restassured.response.ValidatableResponse;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LoginCourierTest {
    CreateCourier createCourier;
    ClientCourier client;
    LoginCourier loginCourier;
    LoginCourier loginCourierWithoutPassword;
    LoginCourier loginNonExistentUser;
    LoginCourier loginIncorrectLogin;
    DeleteCourier deleteCourier;

    @Before
    public void setUp() {
        createCourier = ClientCourier.randomCourier();
        client = new ClientCourier();
        loginCourier = new LoginCourier(createCourier.getLogin(), createCourier.getPassword());
        loginCourierWithoutPassword = new LoginCourier(createCourier.getLogin(), "");
        loginNonExistentUser = new LoginCourier(ClientCourier.randomString(7), ClientCourier.randomString(7));
        loginIncorrectLogin = new LoginCourier(ClientCourier.randomString(7), createCourier.getPassword());
    }

    @After
    public void tearDown() {
        ValidatableResponse response = client.login(loginCourier);
        int num = response.extract().path("id");
        deleteCourier = new DeleteCourier(num);
        client.delete(deleteCourier);
    }

    //курьер может авторизоваться, статус код 200
    @Test
    public void checkSuccessfulLoginStatusCode() {
        client.create(createCourier);
        ValidatableResponse response = client.login(loginCourier);
        assertEquals(200, response.extract().statusCode());
    }

    //для авторизации нужно передать все обязательные поля
    @Test
    public void checkLoginWithoutRequiredField() {
        client.create(createCourier);
        ValidatableResponse response = given()
                .header("Content-type", "application/json")
                .body(loginCourierWithoutPassword)
                .post(ClientCourier.LOGIN_PATH)
                .then();
        assertEquals("Недостаточно данных для входа", response.extract().path("message"));
    }

    //система вернёт ошибку, если неправильно указать логин или пароль
    @Test
    public void checkLoginIncorrectLogin() {
        client.create(createCourier);
        ValidatableResponse response = given()
                .header("Content-type", "application/json")
                .body(loginIncorrectLogin)
                .post(ClientCourier.LOGIN_PATH)
                .then();
        assertEquals(404, response.extract().statusCode());
    }

    //если какого-то поля нет, запрос возвращает ошибку
    @Test
    public void checkLoginWithoutRequiredFieldStatusCod() {
        client.create(createCourier);
        ValidatableResponse response = given()
                .header("Content-type", "application/json")
                .body(loginCourierWithoutPassword)
                .post(ClientCourier.LOGIN_PATH)
                .then();
        assertEquals(400, response.extract().statusCode());
    }

    //если авторизоваться под несуществующим пользователем, запрос возвращает ошибку
    @Test
    public void checkLoginNonExistentUser() {
        client.create(createCourier);
        ValidatableResponse response = given()
                .header("Content-type", "application/json")
                .body(loginNonExistentUser)
                .post(ClientCourier.LOGIN_PATH)
                .then();
        assertEquals("Учетная запись не найдена", response.extract().path("message"));
    }

    //успешный запрос возвращает id
    @Test
    public void checkSuccessfulLoginMessage() {
        client.create(createCourier);
        ValidatableResponse response = client.login(loginCourier);
        assertNotNull(response.extract().path("id"));
    }
}