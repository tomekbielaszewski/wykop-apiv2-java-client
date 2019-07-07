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

    public WykopResponse call(int page) {
        return this.call(String.valueOf(page));
    }

    public WykopResponse call(int page, int firstId) {
        return this.call(String.valueOf(page), String.valueOf(firstId));
    }

    public WykopResponse call(String page) {
        WykopRequest request = this.toRequest()
                .addParam(PAGE, page);

        return this.client.execute(request);
    }

    public WykopResponse call(String page, String firstId) {
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
