package pl.grizwold.wykop.resources.entries;

import lombok.Builder;
import lombok.NonNull;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.resources.WykopResource;

public class EntriesActive extends WykopResource {
    private static final String PAGE = "page";

    private final Integer page;

    @Builder
    public EntriesActive(@NonNull Integer page) {
        super(NOT_SECURED);
        this.page = page;
    }

    @Override
    public WykopRequest toRequest() {
        return new WykopRequest(baseUrl + "/Entries/Active/")
                .addNamedParam(PAGE, page);
    }
}
