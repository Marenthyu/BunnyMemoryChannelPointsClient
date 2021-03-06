package de.marenthyu.memedit;

import de.marenthyu.memedit.bunny.BunnyMemoryManager;
import de.marenthyu.twitch.auth.AuthStore;
import de.marenthyu.twitch.auth.oauth.OAuthToken;
import de.marenthyu.twitch.pubsub.PubSubClient;

import java.net.URISyntaxException;
import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class Main {
    private  static final String CLIENT_ID = "ckqn7b448jawx00ar8pp5yq8l6a9kc";

    public static void main(String[] args) {

        AuthStore.init(CLIENT_ID);
        boolean needsToken = !AuthStore.hasUserToken();
        if (needsToken) {
            AuthStore.requestNewUserToken();
        }
        while (!AuthStore.hasUserToken()) {
            try {
                TimeUnit.MILLISECONDS.sleep(200);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        OAuthToken token = AuthStore.getToken();
        PubSubClient pubSub = null;
        try {
            ArrayList<String> topics = new ArrayList<>();
            topics.add("channel-points-channel-v1." + token.getUserID());
            pubSub = new PubSubClient(token, topics);

        } catch (URISyntaxException e) {
            e.printStackTrace();
        }

        BunnyMemoryManager.init();

        // Okay, we found bunny - time to do some stuff

        assert pubSub != null;
        BunnyMemoryManager.addAllHandlers(pubSub);
        BunnyMemoryManager.addAllDebugHandlers(pubSub);

        System.out.println("============= SETUP COMPLETE! Leaving this running should work now. Close this window to kill it. =============");
    }


}