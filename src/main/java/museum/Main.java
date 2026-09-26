package museum;

import museum.legacy.LegacyLedger;
import java.util.Map;

public final class Main {
    private Main() { }

    public static void main(String[] args) {
        // Composition root: register available backends, do not select one for a request.
        IntakeService service = new IntakeService(
                Map.of("botany", new CentralCatalog(), "expedition", new FieldJournal(),
                        "archive", new LegacyCatalogAdapter(new LegacyLedger(true), "archive")),
                Map.of("standard", StandardAccession::new, "quarantine", QuarantineAccession::new));
        if (args.length != 4) {
            System.err.println("Usage: java -jar target/museum-accession-1.0.0.jar <standard|quarantine> <botany|expedition|archive> <id> <label>");
            System.exit(2);
        }
        try {
            String receipt = service.admit(args[0], new Specimen(args[1], args[2], args[3]));
            System.out.println("Registered: " + receipt + " (workflow=" + args[0] + ", collection=" + args[1] + ")");
        } catch (CatalogException failure) {
            System.err.println(failure.reason() + ": " + failure.getMessage());
            System.exit(1);
        } catch (IllegalArgumentException failure) {
            System.err.println("INVALID_INPUT: " + failure.getMessage());
            System.exit(2);
        }
    }
}
