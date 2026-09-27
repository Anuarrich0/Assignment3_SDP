package cafe;

import java.util.Objects;

/** Bridge Abstraction: order rules depend only on OrderOutput. */
public abstract class CafeOrder {
    protected final OrderOutput output;

    protected CafeOrder(OrderOutput output) {
        this.output = Objects.requireNonNull(output);
    }

    public abstract String place(OrderDetails details) throws OutputException;
}
