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
    private final Map<String, String> params = new HashMap<>();
    private final List<NameValuePair> postParams = new ArrayList<>();

    private String url;
    private String signature;

    public WykopRequest(String url) {
        this.url = url;
    }

    public WykopRequest addPostParam(@NonNull String key, @NonNull String value) {
        postParams.add(new BasicNameValuePair(key, value));
        return this;
    }

    public WykopRequest addParam(@NonNull String key, @NonNull String value) {
        params.put(key, value);
        return this;
    }

    public WykopRequest addParamIfAbsent(String key, String value) {
        params.putIfAbsent(key, value);
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
        return url + paramsToUrl();
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
        return params.entrySet().stream()
                .map(pair -> pair.getKey() + "/" + pair.getValue())
                .collect(Collectors.joining("/"));
    }
}
