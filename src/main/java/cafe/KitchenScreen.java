package cafe;

import java.util.ArrayList;
import java.util.List;

/** Native implementor: a simulated queue on a kitchen screen. */
public final class KitchenScreen implements OrderOutput {
    private final List<String> tickets = new ArrayList<>();

    @Override
    public String send(String ticket) throws OutputException {
        OrderOutput.validate(ticket);
        tickets.add(ticket);
        System.out.println("[KITCHEN SCREEN]\n" + ticket);
        return "SCREEN-" + tickets.size();
    }
}
