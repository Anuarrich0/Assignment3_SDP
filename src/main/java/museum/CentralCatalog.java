package museum;

import java.util.LinkedHashMap;
import java.util.Map;

/** Native implementor: indexed catalog. Storage is in memory for the demo. */
public final class CentralCatalog implements SpecimenCatalog {
    private record Entry(Specimen specimen, int quarantineDays) { }
    private final Map<String, Entry> entries = new LinkedHashMap<>();

    @Override
    public String register(Specimen specimen, int days) throws CatalogException {
        SpecimenCatalog.validate(specimen, days);
        if (entries.containsKey(specimen.key())) {
            throw new CatalogException(CatalogException.Reason.DUPLICATE, "Specimen is already registered");
        }
        entries.put(specimen.key(), new Entry(specimen, days));
        return "CENTRAL-" + entries.size();
    }
}
