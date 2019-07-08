package pl.grizwold.wykop.resources.entries;

import lombok.Builder;
import lombok.NonNull;
import pl.grizwold.wykop.WykopClient;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;
import pl.grizwold.wykop.resources.WykopResource;

import java.util.Optional;

public class EntryAdd extends WykopResource {
    private static final String BODY = "body";
    private static final String EMBED = "embed";
    private static final String ADULT = "adultmedia";

    private final String body;
    private final String fileUrl;
    private final Boolean adult;

    @Builder
    public EntryAdd(@NonNull WykopClient client, @NonNull String body, String fileUrl,
                    Boolean adult) {
        super(client);
        this.body = body;
        this.fileUrl = fileUrl;
        this.adult = adult;
    }

    public WykopResponse call() {
        checkAuthorization();

        WykopRequest request = this.toRequest()
                .addPostParam(BODY, body);
        addIfPresent(EMBED, fileUrl, request);
        addIfPresent(ADULT, adult, request);

        return this.client.execute(request);
    }

    private void addIfPresent(String paramName, Object paramValue, WykopRequest request) {
        Optional.ofNullable(paramValue)
                .map(Object::toString)
                .ifPresent(value -> request.addPostParam(paramName, value));
    }

    @Override
    public WykopRequest toRequest() {
        return new WykopRequest(baseUrl + "/Entries/Add/");
    }
}
