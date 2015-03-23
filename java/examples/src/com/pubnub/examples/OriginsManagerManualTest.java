import com.pubnub.api.Callback;
import com.pubnub.api.Pubnub;
import com.pubnub.api.PubnubException;
import java.util.LinkedHashSet;

public class OriginsManagerManualTest {
    public static void main(String[] args) {
        Pubnub pubnub = new Pubnub("demo", "demo", "demo");

        pubnub.setCacheBusting(false);
        pubnub.setDomain("localhost");
        pubnub.setOriginManagerMaxRetries(3);
        pubnub.setOriginManagerInterval(1);
        pubnub.setOriginManagerIntervalAfterFailure(3);

        LinkedHashSet<String> originsPool = new LinkedHashSet<String>();

        originsPool.add("ps1");
        originsPool.add("ps2");
        originsPool.add("ps3");
        originsPool.add("ps4");
        originsPool.add("ps5");

        try {
            pubnub.setOriginsPool(originsPool);
            pubnub.enableOriginManager();

            pubnub.subscribe("java_test_origin_manager", new Callback() {
                @Override
                public void successCallback(String channel, Object message) {
                    super.successCallback(channel, message);
                }
            });

        } catch (PubnubException e) {
            System.out.println(e.getPubnubError().getErrorString());
        }
    }
}
