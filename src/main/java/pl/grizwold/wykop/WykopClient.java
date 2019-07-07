package pl.grizwold.wykop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import pl.grizwold.wykop.api.Signer;
import pl.grizwold.wykop.model.ApiKey;
import pl.grizwold.wykop.model.ApiParam;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;

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

    public WykopResponse execute(@NonNull WykopRequest request) throws IOException {
        setParams(request);

        String signature = signer.getSignature(request.getUrl(), request.getPayloadValues());
        request.sign(signature);

        CloseableHttpResponse response = client.execute(request.toRequest());
        return toWykopResponse(response);
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

    public WykopClient logout() {
        this.apiKey.setUserKey(null);
        return this;
    }

    private void setParams(WykopRequest request) {
        this.apiParams.forEach(param -> request.addParamIfAbsent(param.key, param.value));
        request.addParamIfAbsent(APPKEY, this.apiKey.getPub());
        Optional.ofNullable(this.apiKey.getUserKey())
                .ifPresent(userkey -> request.addParamIfAbsent(USERKEY, userkey));
    }

    private WykopResponse toWykopResponse(CloseableHttpResponse content) throws IOException {
        String json = EntityUtils.toString(content.getEntity());
        JsonNode jsonNode = om.readTree(json);

        WykopResponse.Error error = null;
        if (jsonNode.has("error")) {
            JsonNode errorNode = jsonNode.get("error");
            int code = errorNode.get("code").asInt();
            String message_pl = errorNode.get("message_pl").asText();
            error = new WykopResponse.Error(code, message_pl);
        }
        return new WykopResponse(json, error);
    }

    @Override
    public void close() throws IOException {
        this.client.close();
    }
}
