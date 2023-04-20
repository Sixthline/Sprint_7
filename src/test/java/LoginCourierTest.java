
import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.CreateCourier;
import model.DeleteCourier;
import model.LoginCourier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.ClientCourier;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class LoginCourierTest {
    private CreateCourier courier;
    private ClientCourier client;
    private LoginCourier loginCourier;
    private LoginCourier loginCourierWithoutPassword;
    private LoginCourier loginIncorrectLogin;

    @Before
    public void setUp() {
        courier = ClientCourier.randomCourier();
        client = new ClientCourier();
        loginCourier = new LoginCourier(courier.getLogin(), courier.getPassword());
        loginCourierWithoutPassword = new LoginCourier(courier.getLogin(), "");
        loginIncorrectLogin = new LoginCourier(ClientCourier.randomString(7), courier.getPassword());
    }

    @After
    public void tearDown() {
        int id = client.login(loginCourier).extract().path("id");
        DeleteCourier deleteCourier = new DeleteCourier(id);
        client.delete(deleteCourier);
    }

    @Test
    @DisplayName("Авторизация курьера")
    public void checkSuccessfulLogin() {
        client.create(courier);
        ValidatableResponse response = client.login(loginCourier);
        assertEquals(200, response.extract().statusCode());
        assertNotNull(response.extract().path("id"));
    }

    @Test
    @DisplayName("Авторизация курьера без обязательного поля")
    public void checkLoginWithoutRequiredField() {
        client.create(courier);
        ValidatableResponse response = client.login(loginCourierWithoutPassword);
        assertEquals(400, response.extract().statusCode());
        assertEquals("Недостаточно данных для входа", response.extract().path("message"));
    }

    @Test
    @DisplayName("Авторизация с использованием неверного логина")
    public void checkLoginIncorrectLogin() {
        client.create(courier);
        ValidatableResponse response = client.login(loginIncorrectLogin);
        assertEquals(404, response.extract().statusCode());
        assertEquals("Учетная запись не найдена", response.extract().path("message"));
    }
}