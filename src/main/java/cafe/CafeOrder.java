package cafe;

import java.util.Objects;

public abstract class CafeOrder {
    protected final OrderOutput output;

    protected CafeOrder(OrderOutput output) {
        this.output = Objects.requireNonNull(output);
    }

    public abstract String place(OrderDetails details) throws OutputException;
}
