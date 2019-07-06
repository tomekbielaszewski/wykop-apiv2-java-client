package pl.grizwold.wykop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import pl.grizwold.wykop.api.Signer;
import pl.grizwold.wykop.model.ApiKey;
import pl.grizwold.wykop.model.WykopResponse;

import java.io.IOException;
import java.io.InputStream;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WykopApiV2ClientApplication {
    private static CloseableHttpClient client = HttpClients.createDefault();
    private static Signer signer = new Signer(new ApiKey("6UMWvIbn0N", "ghnWcqOUNQ"));

    public static void main(String[] args) throws IOException {
//        execute(new HttpGet("https://a2.wykop.pl/Entries/Entry/42463679/"));
        execute(new HttpPost("https://a2.wykop.pl/Entries/Add/"));
    }

    private static void execute(HttpRequestBase request) throws IOException {
        signer.sign(request);

        CloseableHttpResponse closeableHttpResponse = client.execute(request);
        System.out.println(closeableHttpResponse.getStatusLine().toString());

        InputStream content = closeableHttpResponse.getEntity().getContent();
        WykopResponse wykopResponse = getWykopResponse(content);
        System.out.println(wykopResponse.toString());
    }

    private static WykopResponse getWykopResponse(InputStream content) throws IOException {
        String json = IOUtils.toString(content, UTF_8);
        System.out.println(json);

        ObjectMapper om = new ObjectMapper();
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
}
