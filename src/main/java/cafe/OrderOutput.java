package cafe;

public interface OrderOutput {
    String send(String ticket) throws OutputException;

    static void validate(String ticket) throws OutputException {
        if (ticket == null || ticket.isBlank()) {
            throw new OutputException(OutputException.Reason.INVALID_ORDER,
                    "Order ticket must not be empty");
        }
    }
}
