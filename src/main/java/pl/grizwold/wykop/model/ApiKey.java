package pl.grizwold.wykop.model;

import lombok.Value;
import lombok.experimental.NonFinal;
import lombok.experimental.Wither;

@Value
public class ApiKey {
    private String prv;
    private String pub;
    @Wither @NonFinal
    private String userKey;

    public ApiKey(String prv, String pub) {
        this.prv = prv;
        this.pub = pub;
    }
}
