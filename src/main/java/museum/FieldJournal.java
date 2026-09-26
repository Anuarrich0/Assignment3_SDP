package museum;

import java.util.ArrayList;
import java.util.List;

/** Native implementor: append-only expedition journal, in memory for the demo. */
public final class FieldJournal implements SpecimenCatalog {
    private record Entry(Specimen specimen, int quarantineDays) { }
    private final List<Entry> entries = new ArrayList<>();

    @Override
    public String register(Specimen specimen, int days) throws CatalogException {
        SpecimenCatalog.validate(specimen, days);
        if (entries.stream().anyMatch(entry -> entry.specimen().key().equals(specimen.key()))) {
            throw new CatalogException(CatalogException.Reason.DUPLICATE, "Specimen is already registered");
        }
        entries.add(new Entry(specimen, days));
        return "FIELD-" + entries.size();
    }
}
