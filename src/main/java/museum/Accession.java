package museum;

import java.util.Objects;

/** Bridge Abstraction: knows only the Implementor contract. */
public abstract class Accession {
    protected final SpecimenCatalog catalog;

    protected Accession(SpecimenCatalog catalog) {
        this.catalog = Objects.requireNonNull(catalog);
    }

    public abstract String admit(Specimen specimen) throws CatalogException;
}
