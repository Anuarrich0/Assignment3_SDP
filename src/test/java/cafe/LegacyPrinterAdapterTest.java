package cafe;

import cafe.legacy.LegacyPrinter;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import static org.junit.jupiter.api.Assertions.*;

class LegacyPrinterAdapterTest {
    private static class StubPrinter extends LegacyPrinter {
        int status;
        byte[] data;
        int copies;
        int calls;
        RuntimeException failure;
        StubPrinter(int status) { super(true, true); this.status = status; }
        @Override public int printBytes(byte[] data, int copies) {
            this.data = data;
            this.copies = copies;
            calls++;
            if (failure != null) throw failure;
            return status;
        }
    }

    @Test void convertsUtf8AndRequestsExactlyOneCopy() throws Exception {
        StubPrinter stub = new StubPrinter(0);
        assertEquals("LEGACY-1", new LegacyPrinterAdapter(stub).send("Кофе"));
        assertArrayEquals("Кофе".getBytes(StandardCharsets.UTF_8), stub.data);
        assertEquals(1, stub.copies);
        assertEquals(1, stub.calls);
    }

    @ParameterizedTest
    @CsvSource({"-1,PAPER_OUT", "-2,OFFLINE", "-3,INVALID_ORDER", "-99,DEVICE_FAILURE", "1,DEVICE_FAILURE"})
    void translatesKnownAndUnknownStatuses(int status, OutputException.Reason reason) {
        OutputException error = assertThrows(OutputException.class,
                () -> new LegacyPrinterAdapter(new StubPrinter(status)).send("Coffee"));
        assertEquals(reason, error.reason());
        assertNull(error.getCause());
        assertFalse(error.getMessage().contains(Integer.toString(status)));
    }

    @Test void hidesUnexpectedLegacyException() {
        StubPrinter stub = new StubPrinter(0);
        stub.failure = new IllegalStateException("legacy diagnostic");
        OutputException error = assertThrows(OutputException.class,
                () -> new LegacyPrinterAdapter(stub).send("Coffee"));
        assertEquals(OutputException.Reason.DEVICE_FAILURE, error.reason());
        assertNull(error.getCause());
        assertEquals("Could not deliver the order ticket", error.getMessage());
    }

    @Test void failedAttemptDoesNotConsumeReceiptNumber() throws Exception {
        StubPrinter stub = new StubPrinter(-1);
        OrderOutput adapter = new LegacyPrinterAdapter(stub);
        assertThrows(OutputException.class, () -> adapter.send("Coffee"));
        stub.status = 0;
        assertEquals("LEGACY-1", adapter.send("Coffee"));
        assertEquals("LEGACY-2", adapter.send("Tea"));
    }

    @Test void checksUtf8ByteLimitAtBoundary() throws Exception {
        OrderOutput adapter = new LegacyPrinterAdapter(new LegacyPrinter(true, true));
        assertEquals("LEGACY-1", adapter.send("Я".repeat(128)));
        assertEquals(OutputException.Reason.INVALID_ORDER,
                assertThrows(OutputException.class, () -> adapter.send("Я".repeat(129))).reason());
    }

    @Test void realOfflineAndPaperFailuresAreTranslated() {
        assertEquals(OutputException.Reason.OFFLINE, assertThrows(OutputException.class,
                () -> new LegacyPrinterAdapter(new LegacyPrinter(false, true)).send("Tea")).reason());
        assertEquals(OutputException.Reason.PAPER_OUT, assertThrows(OutputException.class,
                () -> new LegacyPrinterAdapter(new LegacyPrinter(true, false)).send("Tea")).reason());
    }
}
