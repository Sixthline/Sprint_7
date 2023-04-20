import io.qameta.allure.junit4.DisplayName;
import io.restassured.response.ValidatableResponse;
import model.CancelOrder;
import model.CreateCourier;
import model.DeleteCourier;
import model.LoginCourier;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import util.ClientCourier;
import util.ClientOrder;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

public class GetOrderListTest {
    private CreateCourier createCourier;
    private ClientCourier clientCourier;
    private ClientOrder clientOrder;
    private LoginCourier loginCourier;

    @Before
    public void setUp() {
        createCourier = ClientCourier.randomCourier();
        clientCourier = new ClientCourier();
        clientOrder = new ClientOrder();
        loginCourier = new LoginCourier(createCourier.getLogin(), createCourier.getPassword());
    }

    @After
    public void tearDown() {
        ValidatableResponse response = clientCourier.login(loginCourier);
        int num = response.extract().path("id");
        DeleteCourier deleteCourier = new DeleteCourier(num);
        clientCourier.delete(deleteCourier);
        CancelOrder cancelOrder = new CancelOrder(clientOrder.createOrder().extract().path("track"));
        clientOrder.cancelOrder(cancelOrder);
    }

    @Test
    @DisplayName("Проверка что список заказов курьера не приходит пустой")
    public void checkOrderListNotNull() {
        clientCourier.create(createCourier);
        int courierId = clientCourier.login(loginCourier).extract().path("id");
        int orderTrack = clientOrder.createOrder().extract().path("track");
        int orderId = clientOrder.getOrderByNumber(orderTrack).extract().path("order.id");
        clientOrder.acceptOrder(orderId, courierId);
        ValidatableResponse response = clientOrder.getCourierOrderList(courierId);
        assertEquals(200, response.extract().statusCode());
        assertNotNull(response.extract().path("orders"));
    }
}