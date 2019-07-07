package pl.grizwold.wykop.api;

import lombok.SneakyThrows;
import pl.grizwold.wykop.model.ApiKey;

import java.security.MessageDigest;

public class Signer {
    private static final char[] LOWERCASE_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private final MessageDigest md5;
    private final ApiKey key;

    @SneakyThrows
    public Signer(ApiKey key) {
        this.key = key;
        this.md5 = MessageDigest.getInstance("MD5");
    }

    public String getSignature(String uri, String payload) {
        String toSign = key.getPrv() + uri + payload;
        byte[] signedBytes = md5.digest(toSign.getBytes());
        return toString(signedBytes);
    }

    private String toString(byte[] bytes) {
        char[] out = new char[bytes.length << 1];

        for (int i = 0, j = 0; i < bytes.length; i++) {
            out[j++] = LOWERCASE_DIGITS[(0xF0 & bytes[i]) >>> 4];
            out[j++] = LOWERCASE_DIGITS[0x0F & bytes[i]];
        }

        return new String(out);
    }
}
