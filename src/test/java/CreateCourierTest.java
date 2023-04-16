import io.restassured.response.ValidatableResponse;
import org.apache.http.HttpStatus;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import static io.restassured.RestAssured.given;
import static org.junit.Assert.assertEquals;

public class CreateCourierTest {
    CreateCourier createCourier;
    ClientCourier client;
    LoginCourier loginCourier;
    DeleteCourier deleteCourier;

    // создаем нужные объекты перед тестами
    @Before
    public void setUp() {
        createCourier = ClientCourier.randomCourier();
        client = new ClientCourier();
        loginCourier = new LoginCourier(createCourier.getLogin(), createCourier.getPassword());
    }

    // удаляем созданного курьера после теста
    @After
    public void tearDown() {
        ValidatableResponse response = client.login(loginCourier);
        int num = response.extract().path("id");
        deleteCourier = new DeleteCourier(num);
        client.delete(deleteCourier);
    }

    // курьера можно создать
    @Test
    public void checkCreateCourier() {
        ValidatableResponse response = client.create(createCourier);
        assertEquals("Неверный статус код", HttpStatus.SC_CREATED, response.extract().statusCode());
        assertEquals("успешный запрос не возвращает ok: true", true, response.extract().path("ok"));
    }

    // нельзя создать двух одинаковых курьеров
    @Test
    public void checkCreatingTwoIdenticalCouriers() {
        client.create(createCourier);
        ValidatableResponse response = client.create(createCourier);
        assertEquals(HttpStatus.SC_CONFLICT, response.extract().statusCode());
    }

    // чтобы создать курьера, нужно передать в ручку все обязательные поля
    @Test
    public void checkCreatingCouriersWithoutRequiredField() {
        ValidatableResponse response1 = client.create(createCourier);
        String json = "{\"login\": \"login\"}";
        ValidatableResponse response2 = given()
                .header("Content-type", "application/json")
                .body(json).post(ClientCourier.CREATE_PATH)
                .then();
        assertEquals("Недостаточно данных для создания учетной записи", response2.extract().path("message"));
    }

    // проверяем что успешный запрос возвращает код ответа "201"
    @Test
    public void checkStatusCod201() {
        ValidatableResponse response = client.create(createCourier);
        assertEquals("Неверный статус код", HttpStatus.SC_CREATED, response.extract().statusCode());
    }

    // успешный запрос возвращает "ok: true"
    @Test
    public void checkCorrectResponse() {
        ValidatableResponse response = client.create(createCourier);
        assertEquals("успешный запрос не возвращает ok: true", true, response.extract().path("ok"));
    }

    // если одного из полей нет, запрос возвращает ошибку
    @Test
    public void checkCreatingCouriersWithoutRequiredFieldStatusCode400() {
        client.create(createCourier);
        String json = "{\"login\": \"login\"}";
        ValidatableResponse response = given()
                .header("Content-type", "application/json")
                .body(json).post(ClientCourier.CREATE_PATH)
                .then();
        assertEquals(400, response.extract().statusCode());
    }

    // если создать пользователя с логином, который уже есть, возвращается ошибка
    @Test
    public void checkCreatingCouriersWithDuplicateLogin() {
        client.create(createCourier);
        ValidatableResponse response = client.create(createCourier);
        assertEquals(HttpStatus.SC_CONFLICT, response.extract().statusCode());
        assertEquals("Этот логин уже используется", response.extract().path("message"));
    }
}