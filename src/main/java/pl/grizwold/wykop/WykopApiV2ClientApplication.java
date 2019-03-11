package pl.grizwold.wykop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.commons.io.IOUtils;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import pl.grizwold.wykop.api.Signer;
import pl.grizwold.wykop.model.ApiKey;
import pl.grizwold.wykop.model.WykopResponse;

import javax.swing.*;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import static java.nio.charset.StandardCharsets.UTF_8;

public class WykopApiV2ClientApplication {

    public static void main(String[] args) throws IOException, NoSuchAlgorithmException {
        CloseableHttpClient client = HttpClients.createDefault();
        CloseableHttpResponse closeableHttpResponse = client.execute(createWykopStreamQuery());
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
        if(jsonNode.has("error")) {
            JsonNode errorNode = jsonNode.get("error");
            int code = errorNode.get("code").asInt();
            String message_pl = errorNode.get("message_pl").asText();
            error = new WykopResponse.Error(code, message_pl);
        }
        return new WykopResponse(json, error);
    }

    private static HttpUriRequest createWykopStreamQuery() {
        HttpGet httpGet = new HttpGet("https://a2.wykop.pl/Entries/Entry/39435487/");

        new Signer(new ApiKey("6UMWvIbn0N", "ghnWcqOUNQ")).sign(httpGet);

        return httpGet;
    }
}
