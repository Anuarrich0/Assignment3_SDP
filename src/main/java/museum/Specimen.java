package museum;

/** The collection is routing data, not a concrete catalog class name. */
public record Specimen(String collection, String inventoryId, String label) {
    public Specimen {
        if (collection == null || collection.isBlank()
                || inventoryId == null || inventoryId.isBlank()
                || label == null || label.isBlank()) {
            throw new IllegalArgumentException("Collection, inventory ID and label are required");
        }
    }

    public String key() {
        return collection + ":" + inventoryId;
    }
}
