package museum;

/** Refined Abstraction: already inspected specimens can enter the collection. */
public final class StandardAccession extends Accession {
    public StandardAccession(SpecimenCatalog catalog) { super(catalog); }

    @Override
    public String admit(Specimen specimen) throws CatalogException {
        return catalog.register(specimen, 0);
    }
}
