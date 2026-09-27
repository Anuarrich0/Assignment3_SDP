package cafe;

import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class OrderServiceTest {
    @Test void stationSelectsOutputForBothOrderTypes() throws Exception {
        OrderService service = new OrderService(
                Map.of("kitchen", ticket -> "screen", "bar", ticket -> "print",
                        "bakery", ticket -> "legacy"),
                Map.of("dine-in", DineInOrder::new, "takeaway", TakeawayOrder::new));
        for (String type : new String[]{"dine-in", "takeaway"}) {
            int table = type.equals("dine-in") ? 7 : 0;
            assertEquals("screen", service.place(type, new OrderDetails("kitchen", "Tea", table)));
            assertEquals("print", service.place(type, new OrderDetails("bar", "Tea", table)));
            assertEquals("legacy", service.place(type, new OrderDetails("bakery", "Tea", table)));
        }
    }

    @Test void unknownInputDoesNotReachOutput() {
        OrderOutput neverCalled = ticket -> { fail("Must not send"); return ""; };
        OrderService service = new OrderService(Map.of("bar", neverCalled),
                Map.of("takeaway", TakeawayOrder::new));
        assertThrows(OutputException.class,
                () -> service.place("unknown", new OrderDetails("bar", "Tea", 0)));
        assertThrows(OutputException.class,
                () -> service.place("takeaway", new OrderDetails("unknown", "Tea", 0)));
        assertThrows(OutputException.class, () -> service.place(null, null));
    }

    @Test void newOrderAndOutputRequireNoExistingClassEdits() throws Exception {
        class DeliveryOrder extends CafeOrder {
            DeliveryOrder(OrderOutput output) { super(output); }
            public String place(OrderDetails details) throws OutputException {
                return output.send("COURIER: " + details.items());
            }
        }
        OrderOutput newOutput = ticket -> "NEW: " + ticket;
        OrderService service = new OrderService(Map.of("dispatch", newOutput),
                Map.of("delivery", DeliveryOrder::new));
        assertEquals("NEW: COURIER: Pizza",
                service.place("delivery", new OrderDetails("dispatch", "Pizza", 0)));
    }
}
