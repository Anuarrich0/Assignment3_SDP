package cafe;

public record OrderDetails(String station, String items, int tableNumber) {
    public OrderDetails {
        if (station == null || station.isBlank() || items == null || items.isBlank()) {
            throw new IllegalArgumentException("Station and items are required");
        }
        if (tableNumber < 0) {
            throw new IllegalArgumentException("Table number cannot be negative");
        }
        if (tableNumber > 80){
            throw new IllegalArgumentException("Tables can not be more than 80")
        }
    }
}
