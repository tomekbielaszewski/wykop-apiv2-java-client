package pl.grizwold.wykop.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;

@Getter
@EqualsAndHashCode
public class ApiKey {
    private String pub;
    private String prv;
    @Setter
    private String userKey;

    public ApiKey(String pub, String prv) {
        this.prv = prv;
        this.pub = pub;
    }
}
