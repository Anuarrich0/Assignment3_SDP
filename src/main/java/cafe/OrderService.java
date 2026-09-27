package cafe;

import java.util.Map;
import java.util.function.Function;

public final class OrderService {
    private final Map<String, OrderOutput> outputs;
    private final Map<String, Function<OrderOutput, CafeOrder>> orderTypes;

    public OrderService(Map<String, OrderOutput> outputs,
                        Map<String, Function<OrderOutput, CafeOrder>> orderTypes) {
        this.outputs = Map.copyOf(outputs);
        this.orderTypes = Map.copyOf(orderTypes);
    }

    public String place(String type, OrderDetails details) throws OutputException {
        if (details == null || type == null) {
            throw new OutputException(OutputException.Reason.INVALID_ORDER,
                    "Order type and details are required");
        }
        OrderOutput output = outputs.get(details.station());
        Function<OrderOutput, CafeOrder> factory = orderTypes.get(type);
        if (output == null || factory == null) {
            throw new OutputException(OutputException.Reason.INVALID_ORDER,
                    "Unknown preparation station or order type");
        }
        return factory.apply(output).place(details);
    }
}
