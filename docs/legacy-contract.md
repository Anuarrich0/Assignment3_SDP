# Frozen legacy component

`src/main/java/museum/legacy/LegacyLedger.java` is a standalone simulated legacy API.
It was established as the adaptee and is not edited to implement `SpecimenCatalog`.
It imports no types from the modern contract. Its API is intentionally documented here
so the adapter can be reviewed against a fixed specification.

SHA-256 of the original component (UTF-8 file bytes):
`04F518153F289E66D11C4D762D8ACE97513F58628BF11C4E01ED047387B30431`

`accession(int holdDays, byte[] description, long number)`:

| Condition | Result |
|---|---|
| New valid specimen | Positive integer receipt |
| Existing numeric identifier | -1 |
| Identifier outside 1..999999, empty/null label, more than 80 bytes, hold outside 0..365 | -2 |
| Constructed with `online=false` | -3 |

Labels passed by the adapter use UTF-8. One instance is one collection. Offline validation
happens first. No modern record, checked exception or string receipt is supported natively.
The adapter also handles zero/unknown statuses and unexpected runtime exceptions
defensively. JVM failures such as `OutOfMemoryError` are not application failures and
are not swallowed.
