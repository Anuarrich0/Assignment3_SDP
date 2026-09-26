package museum;

import java.util.Map;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

class IntakeServiceTest {
    @Test void collectionInputSelectsEachRegisteredImplementation() throws Exception {
        SpecimenCatalog central = (item, days) -> "central:" + days;
        SpecimenCatalog field = (item, days) -> "field:" + days;
        SpecimenCatalog archive = (item, days) -> "archive:" + days;
        IntakeService service = new IntakeService(Map.of("botany", central, "expedition", field, "archive", archive),
                Map.of("standard", StandardAccession::new, "quarantine", QuarantineAccession::new));
        for (String mode : new String[]{"standard", "quarantine"}) {
            String days = mode.equals("standard") ? "0" : "30";
            assertEquals("central:" + days, service.admit(mode, new Specimen("botany", "1", "Fern")));
            assertEquals("field:" + days, service.admit(mode, new Specimen("expedition", "1", "Fern")));
            assertEquals("archive:" + days, service.admit(mode, new Specimen("archive", "1", "Fern")));
        }
    }

    @Test void unsupportedInputFailsBeforeDelegation() {
        SpecimenCatalog neverCalled = (item, days) -> { fail("Should not delegate"); return ""; };
        IntakeService service = new IntakeService(Map.of("botany", neverCalled), Map.of("standard", StandardAccession::new));
        assertEquals(CatalogException.Reason.INVALID_INPUT, assertThrows(CatalogException.class,
                () -> service.admit("standard", new Specimen("unknown", "1", "Fern"))).reason());
        assertEquals(CatalogException.Reason.INVALID_INPUT, assertThrows(CatalogException.class,
                () -> service.admit("unknown", new Specimen("botany", "1", "Fern"))).reason());
    }

    @Test void newAbstractionAndImplementorNeedNoExistingClassChanges() throws Exception {
        // Extensions live only here; Accession, IntakeService and existing catalogs are unchanged.
        class ResearchAccession extends Accession {
            ResearchAccession(SpecimenCatalog catalog) { super(catalog); }
            public String admit(Specimen item) throws CatalogException { return catalog.register(item, 7); }
        }
        SpecimenCatalog newCatalog = (item, days) -> "RESEARCH-" + days;
        IntakeService service = new IntakeService(Map.of("research", newCatalog),
                Map.of("research-hold", ResearchAccession::new));
        assertEquals("RESEARCH-7", service.admit("research-hold", new Specimen("research", "1", "Fern")));
    }
}
