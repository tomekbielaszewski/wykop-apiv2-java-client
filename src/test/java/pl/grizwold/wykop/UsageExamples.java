package pl.grizwold.wykop;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.TextNode;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.junit.Ignore;
import org.junit.Test;
import pl.grizwold.wykop.model.ApiParam;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;
import pl.grizwold.wykop.resources.Login;
import pl.grizwold.wykop.resources.entries.*;

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

public class UsageExamples {
    private static String PUB = "g8fGxGTnw9";
    private static String PRV = "ceeYWvObPc";
    private static String ACCOUNT = "u0sRR7o4Kw4yX3K5hng8";

    @Test
    public void customHttpClient() {
        CloseableHttpClient httpClient = HttpClients.createSystem();
        WykopClient client = new WykopClient(PUB, PRV, httpClient);
    }

    @Test
    public void definingGlobalParams() {
        WykopRequest wykopRequest = new WykopRequest("https://a2.wykop.pl/Entries/Entry/42463679/");

        WykopClient wykopClient = new WykopClient(PUB, PRV);
        wykopClient.set(ApiParam.OUTPUT_CLEAR);

        WykopResponse wykopResponse = wykopClient.execute(wykopRequest);
        System.out.println(wykopResponse.toString());
    }

    @Test
    public void callingPostEndpoint() {
        WykopRequest wykopRequest = new WykopRequest("https://a2.wykop.pl/Login/Index/")
                .addPostParam("accountkey", ACCOUNT);

        WykopClient wykopClient = new WykopClient(PUB, PRV);
        wykopClient.set(ApiParam.OUTPUT_CLEAR);

        WykopResponse wykopResponse = wykopClient.execute(wykopRequest);
        System.out.println(wykopResponse.toString());
    }

    @Test
    public void loggingIn() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new Login(client, ACCOUNT).call();
        //after this call all subsequent requests will be automatically authorized by this client instance
    }

    @Test
    public void loggingIn_modifyingRequestBeforeCall() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopRequest request = new Login(client, ACCOUNT).toRequest();
        request.addNamedParam("data", "full");
        WykopResponse response = client.execute(request);
        System.out.println(response);
        //In this example you need to extract userkey from response by yourself and then set it in the client instance
    }

    @Test
    public void entriesStream() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new EntriesStream(client).call("1", "42485191");
        System.out.println(response);
    }

    @Test
    public void entriesHot() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new EntriesHot(client).call("1", "24");
        System.out.println(response);
    }

    @Test
    public void entriesActive() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new EntriesActive(client).call("1");
        System.out.println(response);
    }

    @Test
    public void entriesObserved() {
        WykopClient client = new WykopClient(PUB, PRV);
        new Login(client, ACCOUNT).call();
        WykopResponse response = new EntriesObserved(client).call("1");
        System.out.println(response);
    }

    @Test
    public void entryById() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new EntryGet(client).call("42485191");
        System.out.println(response);
    }

    @Test
    @Ignore("Posts an entry on wykop.pl")
    public void addEntry() {
        WykopClient client = new WykopClient(PUB, PRV);
        new Login(client, ACCOUNT).call();
        WykopResponse response = EntryAdd.builder()
                .client(client)
                .body("testtesttesttesttest")
                .adult(true)
                .fileUrl("https://cdn.pixabay.com/photo/2013/07/12/17/47/test-pattern-152459_960_720.png")
                .build()
                .call();
        System.out.println(response);
    }

    @Test
    public void notThrowingExceptionWhenApiErrorOccurs() {
        WykopClient client = new WykopClient("123456789", "123456789");
        client.setThrowOnApiError(false);
        WykopResponse response = new EntryGet(client).call("42485191");
        System.out.println(response);
    }

    @Test
    @Ignore("Long running test case")
    public void massiveEntriesStream() throws ExecutionException, InterruptedException {
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
