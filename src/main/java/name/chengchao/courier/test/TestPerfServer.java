package name.chengchao.courier.test;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

import name.chengchao.courier.CourierClient;
import name.chengchao.courier.CourierServer;
import name.chengchao.courier.handler.CustomMessageHandler;
import name.chengchao.courier.protocol.Message;

public class TestPerfServer {

    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);
    private static AtomicLong trafficCounter = new AtomicLong();
    private static AtomicLong msgCounter = new AtomicLong();

    public static void start() throws Exception {

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            System.out.println("msgCounter:" + msgCounter.get() + " ,trafficCounter:" + trafficCounter.get());
        }, 5, 5, TimeUnit.SECONDS);

        new CourierServer(8888, new CustomMessageHandler() {

            @Override
            public Message handle(Message request) {
                trafficCounter.addAndGet(request.getBody().length);
                msgCounter.incrementAndGet();
                return null;
            }
        }).serve();

        CourierClient client = new CourierClient();
        client.start();

        for (int i = 0; i < 10; i++) {
            Thread.sleep(1000);
            Message message = Message.buildRequestMsg(("tell" + i).getBytes());
            client.tell(message, "127.0.0.1", 8888);
        }

    }

}
