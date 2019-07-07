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
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;

import java.io.Closeable;
import java.io.IOException;

public class WykopClient implements Closeable {
    private static final String APPKEY = "appkey";

    private final CloseableHttpClient client;
    private final ObjectMapper om = new ObjectMapper();
    private final ApiKey apiKey;
    private final Signer signer;

    public WykopClient(@NonNull String pub, @NonNull String priv, @NonNull CloseableHttpClient httpClient) {
        this.apiKey = new ApiKey(pub, priv);
        this.signer = new Signer(this.apiKey);
        this.client = httpClient;
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
        request.addParam(APPKEY, this.apiKey.getPub());

        String signature = signer.getSignature(request.getUrl(), request.getPayloadValues());
        request.sign(signature);

        CloseableHttpResponse response = client.execute(request.toRequest());
        return toWykopResponse(response);
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
