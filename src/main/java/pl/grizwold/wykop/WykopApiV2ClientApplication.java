package pl.grizwold.wykop;

import pl.grizwold.wykop.model.WykopRequest;
import pl.grizwold.wykop.model.WykopResponse;

import java.io.IOException;

public class WykopApiV2ClientApplication {

    public static void main(String[] args) throws IOException {
//        execute(new HttpGet("https://a2.wykop.pl/Entries/Entry/42463679/"));

        WykopRequest wykopRequest = new WykopRequest("https://a2.wykop.pl/Login/Index/")
                .addPostParam("accountkey", "KkO9fA3o2uRviR4CVfVn")
                .addParam("appkey", "kUhKLnGypc");

        WykopClient wykopClient = new WykopClient("kUhKLnGypc", "vbrbQmciBU");

        WykopResponse wykopResponse = wykopClient.execute(wykopRequest);
        System.out.println(wykopResponse.toString());
    }
}
