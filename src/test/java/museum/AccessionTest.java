package museum;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class AccessionTest {
    private final Specimen specimen = new Specimen("botany", "42", "Dried fern");

    private static class RecordingCatalog implements SpecimenCatalog {
        Specimen received;
        int days;
        int calls;
        public String register(Specimen specimen, int quarantineDays) {
            received = specimen;
            days = quarantineDays;
            calls++;
            return "receipt";
        }
    }

    @Test void standardDelegatesExactlyOnceWithNoHold() throws Exception {
        RecordingCatalog stub = new RecordingCatalog();
        assertEquals("receipt", new StandardAccession(stub).admit(specimen));
        assertSame(specimen, stub.received);
        assertEquals(0, stub.days);
        assertEquals(1, stub.calls);
    }

    @Test void quarantineDelegatesExactlyOnceWithThirtyDays() throws Exception {
        RecordingCatalog stub = new RecordingCatalog();
        assertEquals("receipt", new QuarantineAccession(stub).admit(specimen));
        assertSame(specimen, stub.received);
        assertEquals(30, stub.days);
        assertEquals(1, stub.calls);
    }

    @Test void bothWorkflowsPreserveContractFailure() {
        CatalogException expected = new CatalogException(CatalogException.Reason.UNAVAILABLE, "Unavailable");
        SpecimenCatalog stub = (item, days) -> { throw expected; };
        for (Accession flow : new Accession[]{new StandardAccession(stub), new QuarantineAccession(stub)}) {
            assertSame(expected, assertThrows(CatalogException.class, () -> flow.admit(specimen)));
        }
    }
}
