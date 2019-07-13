package pl.grizwold.wykop.model;

import lombok.NonNull;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.message.BasicNameValuePair;

import java.util.*;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WykopRequest {
    private final Map<String, String> namedParams = new HashMap<>();
    private final List<NameValuePair> postParams = new ArrayList<>();
    private final List<String> apiParams = new ArrayList<>();

    private String url;
    private String signature;

    public WykopRequest(String url) {
        this.url = url;
    }

    public WykopRequest addPostParam(@NonNull String key, @NonNull String value) {
        postParams.add(new BasicNameValuePair(key, value));
        return this;
    }

    public WykopRequest addPostParam(@NonNull String key, @NonNull Object value) {
        postParams.add(new BasicNameValuePair(key, String.valueOf(value)));
        return this;
    }

    public WykopRequest addApiParam(@NonNull String value) {
        apiParams.add(value);
        return this;
    }

    public WykopRequest addApiParam(@NonNull Object value) {
        apiParams.add(String.valueOf(value));
        return this;
    }

    public WykopRequest addNamedParam(@NonNull String key, @NonNull String value) {
        namedParams.put(key, value);
        return this;
    }

    public WykopRequest addNamedParam(@NonNull String key, @NonNull Object value) {
        namedParams.put(key, String.valueOf(value));
        return this;
    }

    public WykopRequest addApiParamIfAbsent(String key, String value) {
        namedParams.putIfAbsent(key, value);
        return this;
    }

    public HttpUriRequest toRequest() {
        Objects.requireNonNull(signature, "Request is not signed");
        HttpRequestBase request = postParams.size() > 0 ? toPostRequest() : toGetRequest();
        request.addHeader("apisign", signature);
        return request;
    }

    public String getPayloadValues() {
        return postParams.stream()
                .map(NameValuePair::getValue)
                .collect(Collectors.joining(","));
    }

    public String getUrl() {
        return url + urlParamsToUrl() + paramsToUrl();
    }

    public void sign(String signature) {
        this.signature = signature;
    }

    private HttpGet toGetRequest() {
        String _url = getUrl();
        return new HttpGet(_url);
    }

    private HttpPost toPostRequest() {
        String _url = getUrl();
        HttpPost httpPost = new HttpPost(_url);
        httpPost.setEntity(new UrlEncodedFormEntity(postParams, UTF_8));
        return httpPost;
    }

    private String paramsToUrl() {
        return namedParams.entrySet().stream()
                .map(pair -> pair.getKey() + "/" + pair.getValue())
                .collect(Collectors.joining("/"));
    }

    private String urlParamsToUrl() {
        String joined = String.join("/", apiParams);
        return joined.length() > 0 ? joined + "/" : "";
    }
}
