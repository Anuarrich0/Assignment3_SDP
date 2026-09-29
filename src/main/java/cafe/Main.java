package cafe;

import cafe.legacy.LegacyPrinter;
import java.util.Map;
import java.util.Scanner;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        try {
            OrderService service = createService();
            String[] input = readInput(args);
            String type = input[0];
            OrderDetails details = parseOrderDetails(input);

            String receipt = service.place(type, details);
            System.out.println("Sent: " + receipt);
        } catch (OutputException failure) {
            System.err.println(failure.reason() + ": " + failure.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException failure) {
            System.err.println("INVALID_INPUT: " + failure.getMessage());
            System.exit(2);
        }
    }

    private static OrderService createService() {
        Map<String, OrderOutput> outputs = Map.of(
                "kitchen", new KitchenScreen(),
                "bar", new ReceiptPrinter(),
                "bakery", new LegacyPrinterAdapter(new LegacyPrinter(true, true)));

        return new OrderService(outputs, Map.of(
                "dine-in", DineInOrder::new,
                "takeaway", TakeawayOrder::new));
    }

    private static String[] readInput(String[] args) {
        if (args.length == 0) {
            return readInput();
        }
        if (args.length != 4) {
            throw new IllegalArgumentException(
                    "Use: <dine-in|takeaway> <kitchen|bar|bakery> <table or 0> <items>");
        }
        return args;
    }

    private static OrderDetails parseOrderDetails(String[] input) {
        String station = input[1];
        int table = Integer.parseInt(input[2]);
        String items = input[3];
        return new OrderDetails(station, items, table);
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
