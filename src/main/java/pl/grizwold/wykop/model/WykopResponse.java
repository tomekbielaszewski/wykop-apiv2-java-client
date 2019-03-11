package pl.grizwold.wykop.model;

import lombok.Value;

@Value
public class WykopResponse {
    private String json;
    private Error error;

    @Value
    public static class Error {
        private int code;
        private String message_pl;
    }
}
