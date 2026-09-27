package cafe;

/** Bridge Implementor and Adapter Target. */
public interface OrderOutput {
    /**
     * Send one nonblank ticket; return a nonblank delivery receipt.
     * Device limits may reject a ticket with INVALID_ORDER.
     * All operational failures must use OutputException.
     */
    String send(String ticket) throws OutputException;

    static void validate(String ticket) throws OutputException {
        if (ticket == null || ticket.isBlank()) {
            throw new OutputException(OutputException.Reason.INVALID_ORDER,
                    "Order ticket must not be empty");
        }
    }
}
