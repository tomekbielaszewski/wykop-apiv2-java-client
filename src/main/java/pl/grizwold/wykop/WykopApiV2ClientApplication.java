package pl.grizwold.wykop;

import pl.grizwold.wykop.model.ApiParam;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;

import java.io.IOException;

public class WykopApiV2ClientApplication {

    public static void main(String[] args) throws IOException {
        callingPostEndpoint();
    }

    public static void definingGlobalParams() throws IOException {
        WykopRequest wykopRequest = new WykopRequest("https://a2.wykop.pl/Entries/Entry/42463679/");

        WykopClient wykopClient = new WykopClient("kUhKLnGypc", "vbrbQmciBU");
        wykopClient.set(ApiParam.OUTPUT_CLEAR);

        WykopResponse wykopResponse = wykopClient.execute(wykopRequest);
        System.out.println(wykopResponse.toString());
    }

    public static void callingPostEndpoint() throws IOException {
        WykopRequest wykopRequest = new WykopRequest("https://a2.wykop.pl/Login/Index/")
                .addPostParam("accountkey", "KkO9fA3o2uRviR4CVfVn");

        WykopClient wykopClient = new WykopClient("kUhKLnGypc", "vbrbQmciBU");
        wykopClient.set(ApiParam.OUTPUT_CLEAR);

        WykopResponse wykopResponse = wykopClient.execute(wykopRequest);
        System.out.println(wykopResponse.toString());
    }
}
