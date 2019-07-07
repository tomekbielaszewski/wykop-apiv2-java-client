package pl.grizwold.wykop.model;

import lombok.NonNull;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.*;
import org.apache.http.message.BasicNameValuePair;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WykopRequest {
    private final List<NameValuePair> params = new ArrayList<>();
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
        params.add(new BasicNameValuePair(key, value));
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
        return params.stream()
                .map(pair -> pair.getName() + "," + pair.getValue())
                .collect(Collectors.joining(","));
    }
}
