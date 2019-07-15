package pl.grizwold.wykop.resources.entries;

import lombok.NonNull;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.resources.WykopResource;

public class EntryVoteRemove extends WykopResource {
    public final Long id;

    public EntryVoteRemove(@NonNull Long id) {
        super(NOT_SECURED);
        this.id = id;
    }

    @Override
    public WykopRequest toRequest() {
        return new WykopRequest(baseUrl + "/Entries/VoteRemove/")
                .addApiParam(id);
    }
}
