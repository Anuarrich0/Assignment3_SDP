package cafe;

/** Native implementor: a simulated modern text printer. */
public final class ReceiptPrinter implements OrderOutput {
    private int printedTickets;

    @Override
    public String send(String ticket) throws OutputException {
        OrderOutput.validate(ticket);
        System.out.println("[RECEIPT]\n--------------------\n"
                + ticket + "\n--------------------");
        printedTickets++;
        return "PRINT-" + printedTickets;
    }
}
