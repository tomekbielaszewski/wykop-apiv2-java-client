package pl.grizwold.wykop.resources.entries;

import pl.grizwold.wykop.WykopClient;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;
import pl.grizwold.wykop.resources.WykopResource;

import java.io.IOException;

public class EntriesStream extends WykopResource {
    private static final String PAGE = "page";
    private static final String FIRST_ID = "firstid";

    public EntriesStream(WykopClient client) {
        super(client);
    }

    public WykopResponse call(String page) throws IOException {
        WykopRequest request = this.toRequest()
                .addParam(PAGE, page);

        return this.client.execute(request);
    }

    public WykopResponse call(String page, String firstId) throws IOException {
        WykopRequest request = this.toRequest()
                .addParam(PAGE, page)
                .addParam(FIRST_ID, firstId);

        return this.client.execute(request);
    }

    @Override
    public WykopRequest toRequest() {
        return new WykopRequest(baseUrl + "/Entries/Stream/");
    }
}
