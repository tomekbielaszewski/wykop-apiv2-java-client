package pl.grizwold.wykop.resources.entries;

import pl.grizwold.wykop.WykopClient;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;
import pl.grizwold.wykop.resources.WykopResource;

public class EntriesObserved extends WykopResource {
    private static final String PAGE = "page";

    public EntriesObserved(WykopClient client) {
        super(client);
    }

    public WykopResponse call(int page) {
        return this.call(String.valueOf(page));
    }

    public WykopResponse call(String page) {
        WykopRequest request = this.toRequest()
                .addParam(PAGE, page);

        return this.client.execute(request);
    }

    @Override
    public WykopRequest toRequest() {
        return new WykopRequest(baseUrl + "/Entries/Observed/");
    }
}
