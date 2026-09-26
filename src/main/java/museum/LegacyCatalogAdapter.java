package museum;

import museum.legacy.LegacyLedger;
import java.nio.charset.StandardCharsets;
import java.util.Objects;

/** Object Adapter: conversion and legacy failure handling stay at this boundary. */
public final class LegacyCatalogAdapter implements SpecimenCatalog {
    private final LegacyLedger ledger;
    private final String collection;

    public LegacyCatalogAdapter(LegacyLedger ledger, String collection) {
        this.ledger = Objects.requireNonNull(ledger);
        this.collection = Objects.requireNonNull(collection);
    }

    @Override
    public String register(Specimen specimen, int days) throws CatalogException {
        SpecimenCatalog.validate(specimen, days);
        // Reject rather than silently merging IDs such as "007" and "7".
        if (!collection.equals(specimen.collection())
                || !specimen.inventoryId().matches("[1-9][0-9]{0,5}")) {
            throw new CatalogException(CatalogException.Reason.INVALID_INPUT,
                    "This catalog requires its assigned collection and a numeric ID from 1 to 999999");
        }
        int result;
        try {
            result = ledger.accession(days, specimen.label().getBytes(StandardCharsets.UTF_8),
                    Long.parseLong(specimen.inventoryId()));
        } catch (RuntimeException failure) {
            // Deliberately do not expose the legacy exception type/message/cause.
            throw new CatalogException(CatalogException.Reason.INTERNAL, "Catalog operation failed");
        }
        if (result > 0) return "ARCHIVE-" + result;
        throw switch (result) {
            case LegacyLedger.DUPLICATE -> new CatalogException(CatalogException.Reason.DUPLICATE,
                    "Specimen is already registered");
            case LegacyLedger.INVALID -> new CatalogException(CatalogException.Reason.INVALID_INPUT,
                    "Specimen data exceeds this catalog's supported limits");
            case LegacyLedger.OFFLINE -> new CatalogException(CatalogException.Reason.UNAVAILABLE,
                    "Catalog is temporarily unavailable");
            default -> new CatalogException(CatalogException.Reason.INTERNAL, "Catalog operation failed");
        };
    }
}
