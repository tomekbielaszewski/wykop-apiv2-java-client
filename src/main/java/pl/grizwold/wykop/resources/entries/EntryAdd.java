package pl.grizwold.wykop.resources.entries;

import lombok.Builder;
import lombok.NonNull;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.resources.WykopResource;

public class EntryAdd extends WykopResource {
    private static final String BODY = "body";
    private static final String EMBED = "embed";
    private static final String ADULT = "adultmedia";

    private final String body;
    private final String fileUrl;
    private final Boolean adult;

    @Builder
    public EntryAdd(@NonNull String body, String fileUrl,
                    Boolean adult) {
        super(SECURED);
        this.body = body;
        this.fileUrl = fileUrl;
        this.adult = adult;
    }

    @Override
    public WykopRequest toRequest() {
        WykopRequest request = new WykopRequest(baseUrl + "/Entries/Add/")
                .addPostParam(BODY, body);
        ifPresent(fileUrl, () -> request.addPostParam(EMBED, fileUrl));
        ifPresent(adult, () -> request.addPostParam(EMBED, adult));
        return request;
    }
}
