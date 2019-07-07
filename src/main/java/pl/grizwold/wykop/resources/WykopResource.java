package pl.grizwold.wykop.resources;

import pl.grizwold.wykop.model.WykopRequest;

public abstract class WykopResource {
    protected static String baseUrl = "https://a2.wykop.pl";

    public static void setBaseUrl(String baseUrl) {
        WykopResource.baseUrl = baseUrl;
    }

    public abstract WykopRequest toRequest();
}
