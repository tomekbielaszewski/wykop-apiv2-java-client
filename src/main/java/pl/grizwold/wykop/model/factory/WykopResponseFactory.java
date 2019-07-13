package pl.grizwold.wykop.model.factory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.SneakyThrows;
import pl.grizwold.wykop.model.WykopResponse;

public class WykopResponseFactory {

    @SneakyThrows
    public WykopResponse create(String json) {
        ObjectMapper om = new ObjectMapper();
        JsonNode jsonNode = om.readTree(json);

        WykopResponse.ApiError error = parseApiError(jsonNode);
        String data = parseData(om, jsonNode);

        return new WykopResponse(json, error, data);
    }

    private String parseData(ObjectMapper om, JsonNode jsonNode) throws JsonProcessingException {
        if (jsonNode.has("data")) {
            JsonNode dataNode = jsonNode.get("data");
            return om.writeValueAsString(dataNode);
        }
        return null;
    }

    private WykopResponse.ApiError parseApiError(JsonNode jsonNode) {
        if (jsonNode.has("error")) {
            JsonNode errorNode = jsonNode.get("error");
            int code = errorNode.get("code").asInt();
            String message_pl = errorNode.get("message_pl").asText();
            return new WykopResponse.ApiError(code, message_pl);
        }
        return null;
    }
}
