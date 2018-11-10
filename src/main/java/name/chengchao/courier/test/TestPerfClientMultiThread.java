package name.chengchao.courier.test;

import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import name.chengchao.courier.CourierClient;
import name.chengchao.courier.protocol.Message;
import name.chengchao.courier.protocol.MessageHead;

public class TestPerfClientMultiThread {

    private static ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(1);

    private static ExecutorService executorService = Executors.newFixedThreadPool(10);

    public static void main(String[] args) throws Exception {
        Scanner scan = new Scanner(System.in);
        System.out.println("remote ip:");
        String ip = scan.nextLine();
        System.out.println("send msg count:");
        Integer count = Integer.valueOf(scan.nextLine());
        System.out.println("msg size(KB):");
        Integer size = Integer.valueOf(scan.nextLine());
        byte[] body = new byte[size * 1000];
        System.out.println("sleep per msg(ms):");
        Integer sleepMs = Integer.valueOf(scan.nextLine());

        scan.close();

        System.out.println("------------- args --------------");
        System.out.println("remote ip:" + ip);
        System.out.println("send msg count:" + count);
        System.out.println("msg size(KB):" + size);
        System.out.println("sleep per msg(ms):" + sleepMs);
        System.out.println("---------------------------------");

        CourierClient client = new CourierClient();
        client.start();

        scheduledExecutorService.scheduleAtFixedRate(() -> {
            System.out.println("success:" + client.getMsgSuccessCount());
            System.out.println("error:" + client.getMsgErrorCount());
            Throwable cause = client.getLastCause();
            if (null != cause) {
                System.out.println("errorMsg:" + cause.getMessage());
                cause.printStackTrace();
            }
        }, 2, 2, TimeUnit.SECONDS);

        for (int i = 0; i < count; i++) {
            if (sleepMs > 0) {
                Thread.sleep(sleepMs);
            }
            executorService.execute(() -> {
                MessageHead head = MessageHead.buildMessageHead();
                Message message = new Message(head, body);
                client.tell(message, ip, 8888);
            });

        }

    }

}
