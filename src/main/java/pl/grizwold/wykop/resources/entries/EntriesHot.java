package pl.grizwold.wykop.resources.entries;

import lombok.Builder;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.resources.WykopResource;

public class EntriesHot extends WykopResource {
    private static final String PAGE = "page";
    private static final String PERIOD = "period";

    private final Integer page;
    private final Integer period;

    @Builder
    public EntriesHot(Integer page, Integer period) {
        super(NOT_SECURED);
        this.page = page;
        this.period = period;
    }

    @Override
    public WykopRequest toRequest() {
        return new WykopRequest(baseUrl + "/Entries/Hot/")
                .addNamedParam(PAGE, page)
                .addNamedParam(PERIOD, period);
    }
}
