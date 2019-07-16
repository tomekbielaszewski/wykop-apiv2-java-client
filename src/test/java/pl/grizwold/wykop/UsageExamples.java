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
        WykopResponse response = new Login(ACCOUNT).call(client);
        //after this call all subsequent requests will be automatically authorized by this client instance
    }

    @Test
    public void loggingIn_modifyingRequestBeforeCall() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopRequest request = new Login(ACCOUNT).toRequest();
        request.addNamedParam("data", "full");
        WykopResponse response = client.execute(request);
        System.out.println(response);
        //In this example you need to extract userkey from response by yourself and then set it in the client instance
    }

    @Test
    public void entriesStream() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new EntriesStream(1, 42485191L).call(client);
        System.out.println(response);
    }

    @Test
    public void entriesHot() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new EntriesHot(1, 24).call(client);
        System.out.println(response);
    }

    @Test
    public void entriesActive() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new EntriesActive(1).call(client);
        System.out.println(response);
    }

    @Test
    public void entriesObserved() {
        WykopClient client = new WykopClient(PUB, PRV);
        new Login(ACCOUNT).call(client);
        WykopResponse response = new EntriesObserved(1).call(client);
        System.out.println(response);
    }

    @Test
    public void entryById() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new EntryGet(42485191L).call(client);
        System.out.println(response);
    }

    @Test
    @Ignore("Deletes entry from wykop.pl")
    public void deleteEntry() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new EntryDelete(42485191L).call(client);
        System.out.println(response);
    }

    @Test
    public void entryCommentById() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new EntryCommentGet(150072755L).call(client);
        System.out.println(response);
    }

    @Test
    public void commentUpvotersByCommentId() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new CommentUpvoters(150072755L).call(client);
        System.out.println(response);
    }

    @Test
    public void entryVoteUp() {
        WykopClient client = new WykopClient(PUB, PRV);
        new Login(ACCOUNT).call(client);
        WykopResponse response = new EntryVoteUp(42644451L).call(client);
        System.out.println(response);
    }

    @Test
    public void entryVoteRemove() {
        WykopClient client = new WykopClient(PUB, PRV);
        new Login(ACCOUNT).call(client);
        WykopResponse response = new EntryVoteRemove(42644451L).call(client);
        System.out.println(response);
    }

    @Test
    public void entryUpvoters() {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new EntryUpvoters(42485191L).call(client);
        System.out.println(response);
    }

    @Test
    @Ignore("Posts an entry on wykop.pl")
    public void addEntry() {
        WykopClient client = new WykopClient(PUB, PRV);
        new Login(ACCOUNT).call(client);
        WykopResponse response = EntryAdd.builder()
                .body("testtesttesttesttest")
                .adult(true)
                .fileUrl("https://cdn.pixabay.com/photo/2013/07/12/17/47/test-pattern-152459_960_720.png")
                .build()
                .call(client);
        System.out.println(response);
    }

    @Test
    @Ignore("Edits an entry on wykop.pl")
    public void editEntry() {
        WykopClient client = new WykopClient(PUB, PRV);
        new Login(ACCOUNT).call(client);
        WykopResponse response = EntryEdit.builder()
                .id(42485191L)
                .body("testtesttesttesttest")
                .adult(true)
                .fileUrl("https://cdn.pixabay.com/photo/2013/07/12/17/47/test-pattern-152459_960_720.png")
                .build()
                .call(client);
        System.out.println(response);
    }

    @Test
    public void notThrowingExceptionWhenApiErrorOccurs() {
        WykopClient client = new WykopClient("123456789", "123456789");
        client.setThrowOnApiError(false);
        WykopResponse response = new EntryGet(42485191L).call(client);
        System.out.println(response);
    }

    @Test
    @Ignore("Long running test case")
    public void massiveEntriesStream() throws ExecutionException, InterruptedException {
        WykopClient client = new WykopClient(PUB, PRV);
        ObjectMapper om = new ObjectMapper();
        ForkJoinPool forkJoinPool = new ForkJoinPool(8);
        forkJoinPool.submit(() -> {
            List<Object> ids = IntStream.rangeClosed(1, 250) //pagination starts from 1 on wykop.pl
                    .parallel()
                    .mapToObj(page -> EntriesStream.builder().page(page).build())
                    .map(res -> res.call(client))
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
