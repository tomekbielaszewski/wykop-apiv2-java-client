package pl.grizwold.wykop.resources.entries;

import pl.grizwold.wykop.WykopClient;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;
import pl.grizwold.wykop.resources.WykopResource;

import java.io.IOException;

public class EntriesHot extends WykopResource {
    private static final String PAGE = "page";
    private static final String PERIOD = "period";

    public EntriesHot(WykopClient client) {
        super(client);
    }

    public WykopResponse call(String page, String period) throws IOException {
        WykopRequest request = this.toRequest()
                .addParam(PAGE, page)
                .addParam(PERIOD, period);

        return this.client.execute(request);
    }

    @Override
    public WykopRequest toRequest() {
        return new WykopRequest(baseUrl + "/Entries/Hot/");
    }
}
