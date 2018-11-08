package name.chengchao.courier.test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import name.chengchao.courier.CourierClient;
import name.chengchao.courier.protocol.Message;
import name.chengchao.courier.protocol.MessageHead;

public class TestPerfClient {

    final static byte[] test = new byte[1000000];

    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    public static void main(String[] args) throws Exception {

        CourierClient client = new CourierClient();
        client.start();

        for (int i = 0; i < 200; i++) {
            Thread.sleep(1);
            MessageHead head = MessageHead.buildMessageHead();
            Message message = new Message(head, test);
            client.tell(message, "10.125.3.20", 8888);

            if (i % 100 == 0) {
                System.out.println(i);
            }
        }

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            System.out.println("success:" + client.getMsgSuccessCount());
            System.out.println("error:" + client.getMsgErrorCount());
        }, 2, 2, TimeUnit.SECONDS);

    }

}
