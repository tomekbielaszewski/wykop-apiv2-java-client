package pl.grizwold.wykop.resources;

import pl.grizwold.wykop.WykopClient;
import pl.grizwold.wykop.model.WykopRequest;

public abstract class WykopResource {
    protected static String baseUrl = "https://a2.wykop.pl";

    protected final WykopClient client;

    public WykopResource(WykopClient client) {
        this.client = client;
    }

    public abstract WykopRequest toRequest();

    protected void checkAuthorization() {
        if(!this.client.isLoggedIn()) {
            throw new IllegalStateException("Operation requires logging in!");
        }
    }

    public static void setBaseUrl(String baseUrl) {
        WykopResource.baseUrl = baseUrl;
    }
}
