# Museum specimen accession — Adapter and Bridge

Java 17+ / Maven 3.9+. Assignment 3: two intake policies and three catalog backends
in one system. Required complexity module: **dynamic implementor selection**.

## Build and test

```powershell
mvn clean verify
```

This single command compiles the source, runs JUnit 5 tests, and builds an executable JAR.
The first build needs internet access for Maven dependencies. Tests alone: `mvn test`.

## Run

```powershell
java -jar target/museum-accession-1.0.0.jar standard botany B-12 "Dried fern"
java -jar target/museum-accession-1.0.0.jar quarantine expedition E-8 "Moss sample"
java -jar target/museum-accession-1.0.0.jar quarantine archive 42 "Historic herbarium"
```

Expected receipts: `CENTRAL-1`, `FIELD-1`, `ARCHIVE-1`.
Input order: workflow, collection, inventory ID, label.
The collection determines the backend at runtime; the caller never chooses a Java class.
Both workflows work with all three collections. The quarantine period (30 days) is an
illustrative policy, not a conservation recommendation. Storage lasts for one process only.

Failure example:

```powershell
java -jar target/museum-accession-1.0.0.jar standard archive abc "Fern"
```

This reports `INVALID_INPUT` and exits with code 1. An unknown collection/workflow also fails.
Duplicate detection and an offline legacy ledger are demonstrated in the tests.

## Pattern roles

| Role | Class |
|---|---|
| Abstraction | `Accession` |
| Refined Abstractions | `StandardAccession`, `QuarantineAccession` |
| Implementor / Adapter Target | `SpecimenCatalog` |
| Native implementations | `CentralCatalog`, `FieldJournal` |
| Third implementation / Object Adapter | `LegacyCatalogAdapter` |
| Adaptee | `legacy.LegacyLedger` |
| Runtime routing | `IntakeService` |
| Startup wiring and CLI | `Main` |

The old API is a self-contained **simulated legacy component**, not a third-party library.
Its numeric identifiers, UTF-8 byte arrays, reversed arguments, integer receipts and error
codes require real conversions. The adapter does not modify the ledger.

## Submission documents

- [UML image](docs/uml.png) — final production structure.
- [Design rationale](docs/design-rationale.md) — short English submission document.

![UML class diagram](docs/uml.png)

## Test coverage

- `AccessionTest`: recording stubs check both policies, arguments, call counts and failures.
- `LegacyCatalogAdapterTest`: conversion, every documented error code, unknown statuses,
  unexpected exceptions, numeric ID boundaries, UTF-8 byte limit and offline state.
- `IntakeServiceTest`: input routing and extension on both axes without changing existing classes.
- `CatalogIntegrationTest`: common contract, duplicates and all six real combinations.
