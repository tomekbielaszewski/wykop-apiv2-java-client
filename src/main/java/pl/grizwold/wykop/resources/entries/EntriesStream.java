package pl.grizwold.wykop.resources.entries;

import lombok.Builder;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.resources.WykopResource;

public class EntriesStream extends WykopResource {
    private static final String PAGE = "page";
    private static final String FIRST_ID = "firstid";

    private final Integer page;
    private final Long firstId;

    @Builder
    public EntriesStream(Integer page, Long firstId) {
        super(NOT_SECURED);
        this.page = page;
        this.firstId = firstId;
    }

    public EntriesStream(Integer page) {
        super(NOT_SECURED);
        this.page = page;
        this.firstId = null;
    }

    @Override
    public WykopRequest toRequest() {
        WykopRequest request = new WykopRequest(baseUrl + "/Entries/Stream/");
        ifPresent(page, () -> request.addNamedParam(PAGE, page));
        ifPresent(firstId, () -> request.addNamedParam(FIRST_ID, firstId));
        return request;
    }
}
