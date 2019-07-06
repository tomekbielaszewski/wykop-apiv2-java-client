package pl.grizwold.wykop.api;

import lombok.SneakyThrows;
import org.apache.http.client.methods.HttpEntityEnclosingRequestBase;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.util.EntityUtils;
import pl.grizwold.wykop.model.ApiKey;

import java.io.IOException;
import java.net.URI;
import java.security.MessageDigest;

public class Signer {
    private static final char[] LOWERCASE_DIGITS = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    private final MessageDigest md5;

    private ApiKey key;

    @SneakyThrows
    public Signer(ApiKey key) {
        this.key = key;
        this.md5 = MessageDigest.getInstance("MD5");
    }

    public <T extends HttpRequestBase> T sign(T request) {
        prepareUri(request);
        String uri = request.getURI().toString();
        String signature = getSignature(uri, "");
        request.addHeader("apisign", signature);
        return request;
    }

    public <T extends HttpEntityEnclosingRequestBase> T sign(T request) throws IOException {
        prepareUri(request);
        String uri = request.getURI().toString();
        String payload = EntityUtils.toString(request.getEntity());
        String signature = getSignature(uri, payload);
        request.addHeader("apisign", signature);
        return request;
    }

    public Signer login(String userkey) {
        this.key = this.key.withUserKey(userkey);
        return this;
    }

    private <T extends HttpRequestBase> void prepareUri(T request) {
        String uri = request.getURI().toString();
        if(!uri.contains("appkey")) {
            uri = removeLastSlash(uri);
            uri += "/appkey/" + key.getPub();
        }
        request.setURI(URI.create(uri));
    }

    private String removeLastSlash(String uri) {
        return uri.charAt(uri.length() - 1) == '/' ? uri.substring(0, uri.length() - 1) : uri;
    }

    private String getSignature(String uri, String payload) {
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
