package museum;

/**
 * Bridge Implementor and Adapter Target.
 * Register once, retain the label and quarantine period, return a nonblank receipt.
 * Negative days/null specimen: INVALID_INPUT; existing identity: DUPLICATE.
 * Backend limits may reject input. Operational failures use CatalogException only.
 */
public interface SpecimenCatalog {
    String register(Specimen specimen, int quarantineDays) throws CatalogException;

    static void validate(Specimen specimen, int days) throws CatalogException {
        if (specimen == null || days < 0) {
            throw new CatalogException(CatalogException.Reason.INVALID_INPUT,
                    "A specimen and a nonnegative quarantine period are required");
        }
    }
}
