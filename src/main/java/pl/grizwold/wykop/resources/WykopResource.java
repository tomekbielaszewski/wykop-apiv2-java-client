package pl.grizwold.wykop.resources;

import lombok.NonNull;
import pl.grizwold.wykop.WykopClient;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;

import java.util.Optional;

public abstract class WykopResource {
    protected static final boolean SECURED = true;
    protected static final boolean NOT_SECURED = !SECURED;

    protected static String baseUrl = "https://a2.wykop.pl";

    private final boolean secured;

    protected WykopResource(boolean secured) {
        this.secured = secured;
    }

    public WykopResponse call(@NonNull WykopClient client) {
        return client.execute(toRequest());
    }

    public abstract WykopRequest toRequest();

    public boolean isSecured() {
        return secured;
    }

    public static void setBaseUrl(String baseUrl) {
        WykopResource.baseUrl = baseUrl;
    }

    public void ifPresent(Object val, Runnable doOnPresent) {
        Optional.ofNullable(val).ifPresent(_val -> doOnPresent.run());
    }
}
