package cafe;

/** Common operational failures, independent of any device API. */
public final class OutputException extends Exception {
    public enum Reason { INVALID_ORDER, PAPER_OUT, OFFLINE, DEVICE_FAILURE }
    private final Reason reason;

    public OutputException(Reason reason, String message) {
        super(message);
        this.reason = reason;
    }

    public Reason reason() { return reason; }
}
