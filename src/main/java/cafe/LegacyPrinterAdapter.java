package cafe;

import cafe.legacy.LegacyPrinter;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

public final class LegacyPrinterAdapter implements OrderOutput {
    private final LegacyPrinter printer;
    private int sentTickets;

    public LegacyPrinterAdapter(LegacyPrinter printer) {
        this.printer = Objects.requireNonNull(printer);
    }

    @Override
    public String send(String ticket) throws OutputException {
        OrderOutput.validate(ticket);
        int status;
        try {
            status = printer.printBytes(ticket.getBytes(StandardCharsets.UTF_8), 1);
        } catch (RuntimeException failure) {
            throw new OutputException(OutputException.Reason.DEVICE_FAILURE,
                    "Could not deliver the order ticket");
        }
        if (status == LegacyPrinter.OK) {
            sentTickets++;
            return "LEGACY-" + sentTickets;
        }
        throw switch (status) {
            case LegacyPrinter.NO_PAPER -> new OutputException(
                    OutputException.Reason.PAPER_OUT, "Printer needs paper");
            case LegacyPrinter.OFFLINE -> new OutputException(
                    OutputException.Reason.OFFLINE, "Order output is offline");
            case LegacyPrinter.BAD_DATA -> new OutputException(
                    OutputException.Reason.INVALID_ORDER, "Ticket exceeds device limits");
            default -> new OutputException(OutputException.Reason.DEVICE_FAILURE,
                    "Could not deliver the order ticket");
        };
    }
}
