package pl.grizwold.wykop.resources.entries;

import lombok.Builder;
import lombok.NonNull;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.resources.WykopResource;

public class EntryEdit extends WykopResource {
    private static final String BODY = "body";
    private static final String EMBED = "embed";
    private static final String ADULT = "adultmedia";

    private final Long id;
    private final String body;
    private final String fileUrl;
    private final Boolean adult;

    @Builder
    public EntryEdit(@NonNull Long id, @NonNull String body, String fileUrl,
                     Boolean adult) {
        super(SECURED);
        this.id = id;
        this.body = body;
        this.fileUrl = fileUrl;
        this.adult = adult;
    }

    @Override
    public WykopRequest toRequest() {
        WykopRequest request = new WykopRequest(baseUrl + "/Entries/Edit/")
                .addApiParam(id)
                .addPostParam(BODY, body);
        ifPresent(fileUrl, () -> request.addPostParam(EMBED, fileUrl));
        ifPresent(adult, () -> request.addPostParam(EMBED, adult));
        return request;
    }
}
