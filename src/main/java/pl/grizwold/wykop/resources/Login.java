package pl.grizwold.wykop.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import pl.grizwold.wykop.WykopClient;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;

import java.io.IOException;
import java.util.concurrent.Callable;

public class Login extends WykopResource implements Callable<WykopResponse> {
    private static final String ACCOUNT_KEY = "accountkey";
    private static final String USER_KEY = "userkey";
    private static final String DATA_NODE = "data";
    private final WykopClient client;
    private final String accountKey;

    public Login(WykopClient client, String accountKey) {
        this.client = client;
        this.accountKey = accountKey;
    }

    @Override
    public WykopResponse call() throws IOException {
        WykopRequest request = new WykopRequest(baseUrl + "/Login/Index/")
                .addPostParam(ACCOUNT_KEY, this.accountKey);

        WykopResponse response = this.client.execute(request);
        saveLogonInformation(response);

        return response;
    }

    private void saveLogonInformation(WykopResponse wykopResponse) throws IOException {
        if (wykopResponse.getError() == null) {
            ObjectMapper om = new ObjectMapper();
            JsonNode jsonNode = om.readTree(wykopResponse.getJson());

            if (jsonNode.has(DATA_NODE)) {
                JsonNode dataNode = jsonNode.get(DATA_NODE);
                String userkey = dataNode.get(USER_KEY).asText();
                this.client.login(userkey);
            }
        }
    }
}