package museum.legacy;

import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 * Standalone simulated legacy API, frozen before writing its adapter.
 * It has no dependency on the museum contract. One ledger belongs to one collection.
 * Positive result = receipt; -1 = duplicate; -2 = invalid data; -3 = offline.
 * IDs: 1..999999, labels: 1..80 UTF-8 bytes, hold: 0..365 days.
 */
public class LegacyLedger {
    public static final int DUPLICATE = -1;
    public static final int INVALID = -2;
    public static final int OFFLINE = -3;
    private record Entry(String label, int holdDays) { }
    private final Map<Long, Entry> entries = new HashMap<>();
    private final boolean online;

    public LegacyLedger(boolean online) { this.online = online; }

    public int accession(int holdDays, byte[] description, long number) {
        if (!online) return OFFLINE;
        if (number < 1 || number > 999999 || description == null
                || description.length == 0 || description.length > 80
                || holdDays < 0 || holdDays > 365) return INVALID;
        if (entries.containsKey(number)) return DUPLICATE;
        entries.put(number, new Entry(new String(description, StandardCharsets.UTF_8), holdDays));
        return entries.size();
    }
}
