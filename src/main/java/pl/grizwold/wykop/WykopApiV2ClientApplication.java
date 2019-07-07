package pl.grizwold.wykop;

import pl.grizwold.wykop.model.ApiParam;
import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;
import pl.grizwold.wykop.resources.Login;

import java.io.IOException;

public class WykopApiV2ClientApplication {
    private static String PUB = "kUhKLnGypc";
    private static String PRV = "vbrbQmciBU";
    private static String ACCOUNT = "KkO9fA3o2uRviR4CVfVn";

    public static void main(String[] args) throws IOException {
        loggingIn();
    }

    public static void definingGlobalParams() throws IOException {
        WykopRequest wykopRequest = new WykopRequest("https://a2.wykop.pl/Entries/Entry/42463679/");

        WykopClient wykopClient = new WykopClient(PUB, PRV);
        wykopClient.set(ApiParam.OUTPUT_CLEAR);

        WykopResponse wykopResponse = wykopClient.execute(wykopRequest);
        System.out.println(wykopResponse.toString());
    }

    public static void callingPostEndpoint() throws IOException {
        WykopRequest wykopRequest = new WykopRequest("https://a2.wykop.pl/Login/Index/")
                .addPostParam("accountkey", ACCOUNT);

        WykopClient wykopClient = new WykopClient(PUB, PRV);
        wykopClient.set(ApiParam.OUTPUT_CLEAR);

        WykopResponse wykopResponse = wykopClient.execute(wykopRequest);
        System.out.println(wykopResponse.toString());
    }

    public static void loggingIn() throws IOException {
        WykopClient client = new WykopClient(PUB, PRV);
        WykopResponse response = new Login(client, ACCOUNT).call();
    }
}
