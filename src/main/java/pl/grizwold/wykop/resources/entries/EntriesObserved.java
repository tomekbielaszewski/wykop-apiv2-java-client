package pl.grizwold.wykop.resources.entries;

import lombok.NonNull;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.resources.WykopResource;

public class EntriesObserved extends WykopResource {
    private static final String PAGE = "page";

    private final Integer page;

    public EntriesObserved(@NonNull Integer page) {
        super(SECURED);
        this.page = page;
    }

    @Override
    public WykopRequest toRequest() {
        return new WykopRequest(baseUrl + "/Entries/Observed/")
                .addNamedParam(PAGE, page);
    }
}
