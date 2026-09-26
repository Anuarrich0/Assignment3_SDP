package museum;

import museum.legacy.LegacyLedger;
import java.util.Map;
import java.util.stream.Stream;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.MethodSource;
import static org.junit.jupiter.api.Assertions.*;

class CatalogIntegrationTest {
    static Stream<SpecimenCatalog> catalogs() {
        return Stream.of(new CentralCatalog(), new FieldJournal(),
                new LegacyCatalogAdapter(new LegacyLedger(true), "archive"));
    }

    @ParameterizedTest @MethodSource("catalogs")
    void allCatalogsRegisterAndRejectDuplicates(SpecimenCatalog catalog) throws Exception {
        Specimen item = new Specimen("archive", "42", "Fern");
        assertFalse(catalog.register(item, 30).isBlank());
        assertEquals(CatalogException.Reason.DUPLICATE,
                assertThrows(CatalogException.class, () -> catalog.register(item, 0)).reason());
    }

    @ParameterizedTest @MethodSource("catalogs")
    void allCatalogsValidateArguments(SpecimenCatalog catalog) {
        assertEquals(CatalogException.Reason.INVALID_INPUT,
                assertThrows(CatalogException.class, () -> catalog.register(null, 0)).reason());
        assertEquals(CatalogException.Reason.INVALID_INPUT,
                assertThrows(CatalogException.class, () -> catalog.register(new Specimen("archive", "1", "Fern"), -1)).reason());
    }

    @Test void realRuntimeSelectionWorksForAllSixCombinations() throws Exception {
        IntakeService service = new IntakeService(Map.of("botany", new CentralCatalog(), "expedition", new FieldJournal(),
                "archive", new LegacyCatalogAdapter(new LegacyLedger(true), "archive")),
                Map.of("standard", StandardAccession::new, "quarantine", QuarantineAccession::new));
        for (String collection : new String[]{"botany", "expedition", "archive"}) {
            assertFalse(service.admit("standard", new Specimen(collection, "1", "Fern")).isBlank());
            assertFalse(service.admit("quarantine", new Specimen(collection, "2", "Moss")).isBlank());
        }
    }
}
