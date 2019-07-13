package pl.grizwold.wykop.resources;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.NonNull;
import lombok.SneakyThrows;
import pl.grizwold.wykop.WykopClient;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;

public class Login extends WykopResource {
    private static final String ACCOUNT_KEY = "accountkey";
    private static final String USER_KEY = "userkey";
    private static final String DATA_NODE = "data";

    private final String accountKey;

    public Login(@NonNull String accountKey) {
        super(NOT_SECURED);
        this.accountKey = accountKey;
    }

    public WykopResponse call(@NonNull WykopClient client) {
        WykopResponse response = super.call(client);
        saveLogonInformation(response, client);
        return response;
    }

    @Override
    public WykopRequest toRequest() {
        return new WykopRequest(baseUrl + "/Login/Index/")
                .addPostParam(ACCOUNT_KEY, this.accountKey);
    }

    @SneakyThrows
    private void saveLogonInformation(WykopResponse wykopResponse, WykopClient client) {
        if (wykopResponse.getError() == null) {
            ObjectMapper om = new ObjectMapper();
            JsonNode jsonNode = om.readTree(wykopResponse.getJson());

            if (jsonNode.has(DATA_NODE)) {
                JsonNode dataNode = jsonNode.get(DATA_NODE);
                String userkey = dataNode.get(USER_KEY).asText();
                client.login(userkey);
            }
        }
    }
}
