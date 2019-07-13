package pl.grizwold.wykop;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.SneakyThrows;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import pl.grizwold.wykop.api.Signer;
import pl.grizwold.wykop.model.ApiKey;
import pl.grizwold.wykop.model.ApiParam;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;
import pl.grizwold.wykop.model.factory.WykopResponseFactory;
import pl.grizwold.wykop.resources.WykopResource;

import java.io.Closeable;
import java.io.IOException;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

public class WykopClient implements Closeable {
    private static final String APPKEY = "appkey";
    private static final String USERKEY = "userkey";

    private final CloseableHttpClient client;
    private final ObjectMapper om = new ObjectMapper();
    private final ApiKey apiKey;
    private final Signer signer;
    private final Set<ApiParam> apiParams;
    private boolean throwOnApiError = true;

    public WykopClient(@NonNull String pub, @NonNull String priv, @NonNull CloseableHttpClient httpClient) {
        this.apiKey = new ApiKey(pub, priv);
        this.signer = new Signer(this.apiKey);
        this.client = httpClient;
        this.apiParams = new HashSet<>();
    }

    public WykopClient(@NonNull String pub, @NonNull String priv, @NonNull String accountKey, @NonNull CloseableHttpClient httpClient) {
        this(pub, priv, httpClient);
        this.apiKey.setAccountKey(accountKey);
    }

    public WykopClient(@NonNull String pub, @NonNull String priv) {
        this(pub, priv, HttpClients.createDefault());
    }

    public WykopClient(@NonNull String pub, @NonNull String priv, @NonNull String accountKey) {
        this(pub, priv, accountKey, HttpClients.createDefault());
    }

    @SneakyThrows
    public WykopResponse execute(@NonNull WykopRequest request) {
        setParams(request);

        String signature = signer.getSignature(request.getUrl(), request.getPayloadValues());
        request.sign(signature);

        CloseableHttpResponse response = client.execute(request.toRequest());
        return toWykopResponse(response);
    }

    public WykopResponse execute(@NonNull WykopResource resource) {
        if (resource.isSecured() && !this.isLoggedIn()) {
            throw new IllegalStateException("Operation requires logging in!");
        }
        return this.execute(resource.toRequest());
    }

    public WykopClient set(ApiParam apiParam) {
        this.apiParams.add(apiParam);
        return this;
    }

    public WykopClient remove(ApiParam apiParam) {
        this.apiParams.remove(apiParam);
        return this;
    }

    public WykopClient login(String userkey) {
        this.apiKey.setUserKey(userkey);
        return this;
    }

    public boolean isLoggedIn() {
        return this.apiKey.getUserKey() != null;
    }

    public WykopClient logout() {
        this.apiKey.setUserKey(null);
        return this;
    }

    public void setThrowOnApiError(boolean throwOnApiError) {
        this.throwOnApiError = throwOnApiError;
    }

    private void setParams(WykopRequest request) {
        this.apiParams.forEach(param -> request.addApiParamIfAbsent(param.key, param.value));
        request.addApiParamIfAbsent(APPKEY, this.apiKey.getPub());
        Optional.ofNullable(this.apiKey.getUserKey())
                .ifPresent(userkey -> request.addApiParamIfAbsent(USERKEY, userkey));
    }

    @SneakyThrows
    private WykopResponse toWykopResponse(CloseableHttpResponse content) {
        String json = EntityUtils.toString(content.getEntity());
        WykopResponse response = new WykopResponseFactory().create(json);

        if (throwOnApiError && response.getError() != null) {
            throw response.getError();
        }

        return response;
    }

    @Override
    public void close() throws IOException {
        this.client.close();
    }
}
