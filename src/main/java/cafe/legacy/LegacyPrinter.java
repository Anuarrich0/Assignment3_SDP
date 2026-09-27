package cafe.legacy;

import java.nio.charset.StandardCharsets;

/**
 * Standalone simulated legacy API. It does not know OrderOutput.
 * Protocol: UTF-8 bytes, 1..256 bytes, 1..3 copies.
 * Status: 0 success, -1 no paper, -2 offline, -3 invalid data.
 */
public class LegacyPrinter {
    public static final int OK = 0;
    public static final int NO_PAPER = -1;
    public static final int OFFLINE = -2;
    public static final int BAD_DATA = -3;
    private final boolean online;
    private final boolean paperAvailable;

    public LegacyPrinter(boolean online, boolean paperAvailable) {
        this.online = online;
        this.paperAvailable = paperAvailable;
    }

    public int printBytes(byte[] data, int copies) {
        if (!online) return OFFLINE;
        if (!paperAvailable) return NO_PAPER;
        if (data == null || data.length == 0 || data.length > 256
                || copies < 1 || copies > 3) return BAD_DATA;
        String text = new String(data, StandardCharsets.UTF_8);
        for (int i = 0; i < copies; i++) {
            System.out.println("[LEGACY PRINTER]\n" + text);
        }
        return OK;
    }
}
