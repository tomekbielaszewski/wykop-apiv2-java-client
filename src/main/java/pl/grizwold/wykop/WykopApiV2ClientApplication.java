package pl.grizwold.wykop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import pl.grizwold.wykop.api.Signer;
import pl.grizwold.wykop.model.ApiKey;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;

import java.io.IOException;

public class WykopApiV2ClientApplication {
    private static CloseableHttpClient client = HttpClients.createDefault();
    private static Signer signer = new Signer(new ApiKey("kUhKLnGypc", "vbrbQmciBU"));
    private static ObjectMapper om = new ObjectMapper();

    public static void main(String[] args) throws IOException {
//        execute(new HttpGet("https://a2.wykop.pl/Entries/Entry/42463679/"));

        WykopRequest wykopRequest = new WykopRequest("https://a2.wykop.pl/Login/Index/")
                .addPostParam("accountkey", "KkO9fA3o2uRviR4CVfVn")
                .addParam("appkey", "kUhKLnGypc");

        signer.sign(wykopRequest);

        CloseableHttpResponse closeableHttpResponse = client.execute(wykopRequest.toRequest());
        System.out.println(closeableHttpResponse.getStatusLine().toString());

        WykopResponse wykopResponse = getWykopResponse(closeableHttpResponse);
        System.out.println(wykopResponse.toString());
    }

    private static WykopResponse getWykopResponse(CloseableHttpResponse content) throws IOException {
        String json = EntityUtils.toString(content.getEntity());
        System.out.println(json);

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
