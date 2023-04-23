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

public class CreateCourierTest {
    private CreateCourier courier;
    private CreateCourier courierWithoutPassword;
    private ClientCourier client;
    private LoginCourier loginCourier;

    // создаем нужные объекты перед тестами
    @Before
    public void setUp() {
        courier = ClientCourier.randomCourier();
        courierWithoutPassword = new CreateCourier("login");
        client = new ClientCourier();
        loginCourier = new LoginCourier(courier.getLogin(), courier.getPassword());
    }

    // удаляем созданного курьера после теста
    @After
    public void tearDown() {
        int num = client.login(loginCourier).extract().path("id");
        DeleteCourier deleteCourier = new DeleteCourier(num);
        client.delete(deleteCourier);
    }

    @Test
    @DisplayName("Создание курьера")
    public void checkCreateCourier() {
        ValidatableResponse response = client.create(courier);
        assertEquals(201, response.extract().statusCode());
        assertEquals(true, response.extract().path("ok"));
    }

    @Test
    @DisplayName("Создание курьера без обязательного передаваемого поля")
    public void checkCreatingCouriersWithoutRequiredField() {
        client.create(courier);
        ValidatableResponse response = client.create(courierWithoutPassword);
        assertEquals(400, response.extract().statusCode());
        assertEquals("Недостаточно данных для создания учетной записи", response.extract().path("message"));
    }

    @Test
    @DisplayName("Создание двух одинаковых курьеров")
    public void checkCreatingCouriersWithDuplicateLogin() {
        client.create(courier);
        ValidatableResponse response = client.create(courier);
        assertEquals(409, response.extract().statusCode());
        assertEquals("Этот логин уже используется", response.extract().path("message"));
    }
}