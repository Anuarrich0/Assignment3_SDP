package museum;

/** Refined Abstraction: uninspected arrivals get a 30-day hold in this demo. */
public final class QuarantineAccession extends Accession {
    public QuarantineAccession(SpecimenCatalog catalog) { super(catalog); }

    @Override
    public String admit(Specimen specimen) throws CatalogException {
        return catalog.register(specimen, 30);
    }
}
