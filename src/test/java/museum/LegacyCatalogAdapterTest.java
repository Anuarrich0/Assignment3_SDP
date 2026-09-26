package museum;

import museum.legacy.LegacyLedger;
import java.nio.charset.StandardCharsets;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.jupiter.params.provider.ValueSource;
import static org.junit.jupiter.api.Assertions.*;

class LegacyCatalogAdapterTest {
    private final Specimen specimen = new Specimen("archive", "42", "Гербарий");

    private static class StubLedger extends LegacyLedger {
        int result;
        int days;
        byte[] description;
        long number;
        int calls;
        RuntimeException failure;
        StubLedger(int result) { super(true); this.result = result; }
        @Override public int accession(int days, byte[] description, long number) {
            calls++;
            this.days = days;
            this.description = description;
            this.number = number;
            if (failure != null) throw failure;
            return result;
        }
    }

    @Test void translatesTypesOrderEncodingAndReceipt() throws Exception {
        StubLedger stub = new StubLedger(19);
        assertEquals("ARCHIVE-19", new LegacyCatalogAdapter(stub, "archive").register(specimen, 30));
        assertEquals(30, stub.days);
        assertEquals(42L, stub.number);
        assertArrayEquals(specimen.label().getBytes(StandardCharsets.UTF_8), stub.description);
        assertEquals(1, stub.calls);
    }

    @ParameterizedTest
    @CsvSource({"-1,DUPLICATE", "-2,INVALID_INPUT", "-3,UNAVAILABLE", "0,INTERNAL", "-99,INTERNAL"})
    void translatesEveryStatusAndUnknownStatuses(int code, CatalogException.Reason reason) {
        LegacyCatalogAdapter adapter = new LegacyCatalogAdapter(new StubLedger(code), "archive");
        CatalogException failure = assertThrows(CatalogException.class, () -> adapter.register(specimen, 0));
        assertEquals(reason, failure.reason());
        assertNull(failure.getCause());
        assertFalse(failure.getMessage().contains(Integer.toString(code)));
    }

    @Test void unexpectedExceptionDoesNotLeakLegacyDetails() {
        StubLedger stub = new StubLedger(1);
        stub.failure = new IllegalStateException("legacy secret diagnostic");
        CatalogException failure = assertThrows(CatalogException.class,
                () -> new LegacyCatalogAdapter(stub, "archive").register(specimen, 0));
        assertEquals(CatalogException.Reason.INTERNAL, failure.reason());
        assertNull(failure.getCause());
        assertEquals("Catalog operation failed", failure.getMessage());
    }

    @ParameterizedTest
    @ValueSource(strings = {"abc", "0", "-1", "007", "1000000", "999999999999999999999"})
    void rejectsUnrepresentableIdsBeforeCallingLegacy(String id) {
        StubLedger stub = new StubLedger(1);
        CatalogException failure = assertThrows(CatalogException.class,
                () -> new LegacyCatalogAdapter(stub, "archive").register(new Specimen("archive", id, "Fern"), 0));
        assertEquals(CatalogException.Reason.INVALID_INPUT, failure.reason());
        assertEquals(0, stub.calls);
    }

    @Test void rejectsWrongCollection() {
        CatalogException failure = assertThrows(CatalogException.class,
                () -> new LegacyCatalogAdapter(new StubLedger(1), "archive")
                        .register(new Specimen("other", "42", "Fern"), 0));
        assertEquals(CatalogException.Reason.INVALID_INPUT, failure.reason());
    }

    @Test void realLegacyEnforcesByteLimitAndHoldLimit() {
        SpecimenCatalog adapter = new LegacyCatalogAdapter(new LegacyLedger(true), "archive");
        // 41 Cyrillic letters are 82 UTF-8 bytes, even though length() is only 41.
        for (Specimen item : new Specimen[]{new Specimen("archive", "1", "Я".repeat(41)), specimen}) {
            int days = item == specimen ? 366 : 0;
            assertEquals(CatalogException.Reason.INVALID_INPUT,
                    assertThrows(CatalogException.class, () -> adapter.register(item, days)).reason());
        }
    }

    @Test void realOfflineLedgerMapsToUnavailable() {
        assertEquals(CatalogException.Reason.UNAVAILABLE,
                assertThrows(CatalogException.class, () -> new LegacyCatalogAdapter(new LegacyLedger(false), "archive")
                        .register(specimen, 0)).reason());
    }
}
