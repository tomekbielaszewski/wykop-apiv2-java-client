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

    public static void setBaseUrl(String baseUrl) {
        WykopResource.baseUrl = baseUrl;
    }
}
