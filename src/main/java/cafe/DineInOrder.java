package cafe;

/** Refined Abstraction: serve the order at a numbered table. */
public final class DineInOrder extends CafeOrder {
    public DineInOrder(OrderOutput output) { super(output); }

    @Override
    public String place(OrderDetails details) throws OutputException {
        if (details == null || details.tableNumber() < 1) {
            throw new OutputException(OutputException.Reason.INVALID_ORDER,
                    "Dine-in orders need a positive table number");
        }
        String ticket = "TABLE " + details.tableNumber() + "\n"
                + details.items() + "\nServe on plates.";
        return output.send(ticket);
    }
}
