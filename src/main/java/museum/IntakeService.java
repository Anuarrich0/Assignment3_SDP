package museum;

import java.util.Map;
import java.util.function.Function;

/** Runtime selection by collection input; registries are injected at startup. */
public final class IntakeService {
    private final Map<String, SpecimenCatalog> catalogs;
    private final Map<String, Function<SpecimenCatalog, Accession>> workflows;

    public IntakeService(Map<String, SpecimenCatalog> catalogs,
                         Map<String, Function<SpecimenCatalog, Accession>> workflows) {
        this.catalogs = Map.copyOf(catalogs);
        this.workflows = Map.copyOf(workflows);
    }

    public String admit(String workflow, Specimen specimen) throws CatalogException {
        SpecimenCatalog.validate(specimen, 0);
        SpecimenCatalog catalog = catalogs.get(specimen.collection());
        Function<SpecimenCatalog, Accession> factory = workflow == null ? null : workflows.get(workflow);
        if (catalog == null || factory == null) {
            throw new CatalogException(CatalogException.Reason.INVALID_INPUT,
                    "Unknown collection or intake workflow");
        }
        return factory.apply(catalog).admit(specimen);
    }
}
