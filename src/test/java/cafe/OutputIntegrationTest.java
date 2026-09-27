package cafe;

import cafe.legacy.LegacyPrinter;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

class OutputIntegrationTest {
    static Stream<OrderOutput> outputs() {
        return Stream.of(new KitchenScreen(), new ReceiptPrinter(),
                new LegacyPrinterAdapter(new LegacyPrinter(true, true)));
    }

    @ParameterizedTest @MethodSource("outputs")
    void outputsAcceptTicketsAndReturnDistinctReceipts(OrderOutput output) throws Exception {
        String first = output.send("Tea");
        String second = output.send("Tea");
        assertFalse(first.isBlank());
        assertFalse(second.isBlank());
        assertNotEquals(first, second);
    }

    @ParameterizedTest @MethodSource("outputs")
    void outputsRejectNullAndBlankTickets(OrderOutput output) {
        for (String ticket : new String[]{null, "", "   "}) {
            assertEquals(OutputException.Reason.INVALID_ORDER,
                    assertThrows(OutputException.class, () -> output.send(ticket)).reason());
        }
    }

    @Test void realOutputsWorkWithBothOrderTypes() throws Exception {
        OrderService service = new OrderService(
                Map.of("kitchen", new KitchenScreen(), "bar", new ReceiptPrinter(),
                        "bakery", new LegacyPrinterAdapter(new LegacyPrinter(true, true))),
                Map.of("dine-in", DineInOrder::new, "takeaway", TakeawayOrder::new));
        for (String station : new String[]{"kitchen", "bar", "bakery"}) {
            assertFalse(service.place("dine-in", new OrderDetails(station, "Cake", 7)).isBlank());
            assertFalse(service.place("takeaway", new OrderDetails(station, "Cake", 0)).isBlank());
        }
    }
}
