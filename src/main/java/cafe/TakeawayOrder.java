package cafe;

/** Refined Abstraction: pack the order instead of serving at a table. */
public final class TakeawayOrder extends CafeOrder {
    public TakeawayOrder(OrderOutput output) { super(output); }

    @Override
    public String place(OrderDetails details) throws OutputException {
        if (details == null || details.tableNumber() != 0) {
            throw new OutputException(OutputException.Reason.INVALID_ORDER,
                    "Takeaway orders use table number 0 (no table)");
        }
        String ticket = "TAKEAWAY\n" + details.items()
                + "\nPack in takeaway containers.";
        return output.send(ticket);
    }
}
