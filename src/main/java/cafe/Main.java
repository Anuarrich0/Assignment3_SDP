package cafe;

import cafe.legacy.LegacyPrinter;
import java.util.Map;
import java.util.Scanner;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        OrderService service = new OrderService(
                Map.of("kitchen", new KitchenScreen(), "bar", new ReceiptPrinter(),
                        "bakery", new LegacyPrinterAdapter(new LegacyPrinter(true, true))),
                Map.of("dine-in", DineInOrder::new, "takeaway", TakeawayOrder::new));
        try {
            String[] input = args.length == 0 ? readInput() : args;
            if (input.length != 4) {
                throw new IllegalArgumentException(
                        "Use: <dine-in|takeaway> <kitchen|bar|bakery> <table or 0> <items>");
            }
            OrderDetails details = new OrderDetails(input[1], input[3],
                    Integer.parseInt(input[2]));
            String receipt = service.place(input[0], details);
            System.out.println("Sent: " + receipt);
        } catch (OutputException failure) {
            System.err.println(failure.reason() + ": " + failure.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException failure) {
            System.err.println("INVALID_INPUT: " + failure.getMessage());
            System.exit(2);
        }
    }

    private static String[] readInput() {
        Scanner scanner = new Scanner(System.in);
        String type = ask(scanner, "Order type (dine-in / takeaway): ");
        String station = ask(scanner, "Station (kitchen / bar / bakery): ");
        String table = type.equals("dine-in") ? ask(scanner, "Table number: ") : "0";
        String items = ask(scanner, "Items: ");
        return new String[]{type, station, table, items};
    }

    private static String ask(Scanner scanner, String prompt) {
        System.out.print(prompt);
        if (!scanner.hasNextLine()) {
            throw new IllegalArgumentException("Input ended before the order was complete");
        }
        return scanner.nextLine().strip();
    }
}
