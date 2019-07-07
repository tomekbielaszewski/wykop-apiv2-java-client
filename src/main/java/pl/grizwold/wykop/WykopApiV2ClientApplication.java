package pl.grizwold.wykop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import pl.grizwold.wykop.model.ApiParam;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;
import pl.grizwold.wykop.resources.Login;
import pl.grizwold.wykop.resources.entries.EntriesHot;
import pl.grizwold.wykop.resources.entries.EntriesStream;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Spliterator;
import java.util.Spliterators;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ForkJoinPool;
import java.util.function.Function;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

public class WykopApiV2ClientApplication {
    private static String PUB = "aNd401dAPp";
    private static String PRV = "3lWf1lCxD6";
    private static String ACCOUNT = "KkO9fA3o2uRviR4CVfVn";

    public static void main(String[] args) throws Exception {
        massiveEntriesStream();
    }

    public static void definingGlobalParams() {
        WykopRequest wykopRequest = new WykopRequest("https://a2.wykop.pl/Entries/Entry/42463679/");

        WykopClient wykopClient = new WykopClient(PUB, PRV);
        wykopClient.set(ApiParam.OUTPUT_CLEAR);

        WykopResponse wykopResponse = wykopClient.execute(wykopRequest);
        System.out.println(wykopResponse.toString());
    }

    public static void callingPostEndpoint() {
        WykopRequest wykopRequest = new WykopRequest("https://a2.wykop.pl/Login/Index/")
                .addPostParam("accountkey", ACCOUNT);

        WykopClient wykopClient = new WykopClient(PUB, PRV);
        wykopClient.set(ApiParam.OUTPUT_CLEAR);

        WykopResponse wykopResponse = wykopClient.execute(wykopRequest);
        System.out.println(wykopResponse.toString());
    }

    public static void loggingIn() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new Login(client, ACCOUNT).call();
        //after this call all subsequent requests will be automatically authorized by this client instance
    }

    public static void loggingIn_modifyingRequestBeforeCall() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopRequest request = new Login(client, ACCOUNT).toRequest();
        request.addParam("data", "full");
        WykopResponse response = client.execute(request);
        System.out.println(response);
        //In this example you need to extract userkey from response by yourself and then set it in the client instance
    }

    public static void entriesStream() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new EntriesStream(client).call("1", "42485191");
        System.out.println(response);
    }

    public static void entriesHot() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new EntriesHot(client).call("1", "24");
        System.out.println(response);
    }

    public static void massiveEntriesStream() throws ExecutionException, InterruptedException {
        WykopClient client = new WykopClient(PUB, PRV);
        EntriesStream entriesStream = new EntriesStream(client);
        ObjectMapper om = new ObjectMapper();
        ForkJoinPool forkJoinPool = new ForkJoinPool(8);
        forkJoinPool.submit(() -> {
            List<Object> ids = IntStream.rangeClosed(1, 250) //pagination starts from 1 on wykop.pl
                    .parallel()
                    .mapToObj(entriesStream::call)
                    .map(WykopResponse::getJson)
                    .map(json -> readJsonTree(json, om))
                    .map(node -> node.get("data"))
                    .map(JsonNode::iterator)
                    .map(iterator -> Spliterators.spliteratorUnknownSize(iterator, Spliterator.CONCURRENT))
                    .map(split -> StreamSupport.stream(split, true))
                    .flatMap(Function.identity())
                    .map(node -> node.get("id"))
                    .map(JsonNode::asText)
                    .collect(Collectors.toList());
            System.out.println("Total entries fetched: " + ids.size());
            HashSet<Object> objects = new HashSet<>(ids);
            System.out.println("Unique entries fetched: " + objects.size());
            System.out.println("Notice that total and unique count differs a lot! Wykop limits pagination to 200 max");
        }).get();
    }

    private static JsonNode readJsonTree(String json, ObjectMapper om) {
        try {
            return om.readTree(json);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return new TextNode("IOException");
    }
}
