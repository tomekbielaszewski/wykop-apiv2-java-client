package pl.grizwold.wykop.resources.entries;

import lombok.NonNull;
import pl.grizwold.wykop.WykopClient;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;
import pl.grizwold.wykop.resources.WykopResource;

public class EntryGet extends WykopResource {
    private static final String PAGE = "page";

    public EntryGet(@NonNull WykopClient client) {
        super(client);
    }

    public WykopResponse call(int id) {
        return this.call(String.valueOf(id));
    }

    public WykopResponse call(String id) {
        WykopRequest request = this.toRequest()
                .addApiParam(id);

        return this.client.execute(request);
    }

    @Override
    public WykopRequest toRequest() {
        return new WykopRequest(baseUrl + "/Entries/Entry/");
    }
}
