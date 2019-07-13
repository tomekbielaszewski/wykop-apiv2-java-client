package pl.grizwold.wykop.model;

import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Value;

@Value
public class WykopResponse {
    private String json;
    private ApiError error;
    private String data;

    @Getter
    @EqualsAndHashCode(callSuper = false)
    public static class ApiError extends RuntimeException {
        private int code;
        private String message_pl;

        public ApiError(int code, String message_pl) {
            super(code + ":" + message_pl);
            this.code = code;
            this.message_pl = message_pl;
        }
    }
}
