package museum;

/** The only operational failure exposed by SpecimenCatalog. */
public final class CatalogException extends Exception {
    public enum Reason { INVALID_INPUT, DUPLICATE, UNAVAILABLE, INTERNAL }
    private final Reason reason;

    public CatalogException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public Reason reason() {
        return reason;
    }
}
