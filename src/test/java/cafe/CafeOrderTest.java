package cafe;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class CafeOrderTest {
    private static class RecordingOutput implements OrderOutput {
        String ticket;
        int calls;
        public String send(String ticket) {
            this.ticket = ticket;
            calls++;
            return "receipt";
        }
    }

    @Test void dineInAddsTableAndServingInstructions() throws Exception {
        RecordingOutput stub = new RecordingOutput();
        String result = new DineInOrder(stub).place(new OrderDetails("kitchen", "Burger", 7));
        assertEquals("receipt", result);
        assertEquals("TABLE 7\nBurger\nServe on plates.", stub.ticket);
        assertEquals(1, stub.calls);
    }

    @Test void takeawayAddsPackingInstructions() throws Exception {
        RecordingOutput stub = new RecordingOutput();
        assertEquals("receipt", new TakeawayOrder(stub).place(new OrderDetails("bar", "Coffee", 0)));
        assertEquals("TAKEAWAY\nCoffee\nPack in takeaway containers.", stub.ticket);
        assertEquals(1, stub.calls);
    }

    @Test void invalidTableDoesNotReachOutput() {
        RecordingOutput stub = new RecordingOutput();
        assertEquals(OutputException.Reason.INVALID_ORDER, assertThrows(OutputException.class,
                () -> new DineInOrder(stub).place(new OrderDetails("kitchen", "Burger", 0))).reason());
        assertEquals(OutputException.Reason.INVALID_ORDER, assertThrows(OutputException.class,
                () -> new TakeawayOrder(stub).place(new OrderDetails("kitchen", "Burger", 7))).reason());
        assertEquals(0, stub.calls);
    }

    @Test void bothOrderTypesPropagateTheCommonFailure() {
        OutputException expected = new OutputException(OutputException.Reason.OFFLINE, "Offline");
        OrderOutput stub = ticket -> { throw expected; };
        assertSame(expected, assertThrows(OutputException.class,
                () -> new DineInOrder(stub).place(new OrderDetails("bar", "Tea", 1))));
        assertSame(expected, assertThrows(OutputException.class,
                () -> new TakeawayOrder(stub).place(new OrderDetails("bar", "Tea", 0))));
    }
}
