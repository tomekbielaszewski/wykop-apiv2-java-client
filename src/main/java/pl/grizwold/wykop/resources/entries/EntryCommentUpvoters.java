package pl.grizwold.wykop.resources.entries;

import lombok.NonNull;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.resources.WykopResource;

public class EntryCommentUpvoters extends WykopResource {
    private final Long id;

    public EntryCommentUpvoters(@NonNull Long id) {
        super(NOT_SECURED);
        this.id = id;
    }

    @Override
    public WykopRequest toRequest() {
        return new WykopRequest(baseUrl + "/Entries/CommentUpvoters/")
                .addApiParam(id);
    }
}
