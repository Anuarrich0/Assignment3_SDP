# Museum specimen accession

## Problem and independent variation

A museum accepts specimens into botanical, expedition and historical collections.
Already inspected arrivals use standard accession; uninspected arrivals receive a
30-day quarantine marker. This duration is an illustrative software policy.
Catalog infrastructure varies separately: a central indexed catalog, a sequential
field journal, and an old numeric ledger. Either intake policy must work with any
catalog. Combining policies and catalogs through subclasses would require six
combinations for two policies and three catalogs, and more as either dimension grows.

## Bridge and Adapter together

`Accession` holds a `SpecimenCatalog`. `StandardAccession` and `QuarantineAccession`
choose the hold duration and delegate registration. `CentralCatalog` and `FieldJournal`
implement the contract directly. `LegacyCatalogAdapter` is its third implementation
and wraps `LegacyLedger`. The abstraction hierarchy imports no legacy types or constants.

Bridge alone would separate policies and catalogs, but would not translate the old
ledger's incompatible protocol. Adapter alone would provide a common catalog interface,
but would not structure the independent policy hierarchy. Here the adapter participates
inside the bridge; the two patterns handle the same registration request.

## Genuine incompatibility and error contract

The standalone simulated legacy component was frozen before adapter integration; it
is not claimed to be an external vendor library. The target accepts
`register(Specimen, int)` and returns a string receipt, with `CatalogException` for
operational failures. The ledger accepts `accession(int, byte[], long)` and returns
an integer receipt or a negative status. The adapter extracts the ID, validates its
canonical numeric representation, encodes the label as UTF-8, reorders arguments,
and constructs a receipt. It rejects IDs such as `007` instead of merging them with `7`.
A ledger is assigned to one collection, which the adapter enforces.

Statuses -1, -2 and -3 become `DUPLICATE`, `INVALID_INPUT` and `UNAVAILABLE`.
Zero, unknown negative statuses and unexpected runtime exceptions become `INTERNAL`.
No raw status or legacy exception message/cause crosses the boundary. Backend-specific
input limits are permitted by the common contract and reported as `INVALID_INPUT`.

## Required complexity module and Open/Closed Principle

Chosen module: **dynamic implementor selection**. `IntakeService` looks up a catalog
using the incoming specimen's collection; `Main` registers available catalogs at
startup but never selects a fixed backend for a request. The same executable accepts
botany, expedition or archive input, including the adapted backend.

A new policy subclasses `Accession`; a new backend implements `SpecimenCatalog`.
Both are supplied through constructor-injected registries. Existing policies,
catalogs and routing logic need no edits. Registration in an application's composition
root is configuration, not a modification to the pattern logic. A test introduces
a seven-day research policy and another backend using new wiring, without editing any
existing production class. The module is one-way adaptation, not a two-way adapter.

## Validation and limitation

JUnit 5 tests use recording stubs for delegation and a stub ledger for each failure
path, plus integration tests for all six combinations. UML names and relationships
match production code. One limitation is process-local, single-threaded storage:
entries disappear when the CLI exits. Durable, concurrent storage would require new
backend implementations; it is outside this pattern-focused prototype.
