package pl.grizwold.wykop.resources.entries;

import lombok.NonNull;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.resources.WykopResource;

public class EntryDelete extends WykopResource {
    public final Long id;

    public EntryDelete(@NonNull Long id) {
        super(SECURED);
        this.id = id;
    }

    @Override
    public WykopRequest toRequest() {
        return new WykopRequest(baseUrl + "/Entries/Delete/")
                .addApiParam(id);
    }
}
