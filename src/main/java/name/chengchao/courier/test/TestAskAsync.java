package name.chengchao.courier.test;

import name.chengchao.courier.CourierClient;
import name.chengchao.courier.CourierServer;
import name.chengchao.courier.ResponseCallback;
import name.chengchao.courier.protocol.Message;
import name.chengchao.courier.protocol.MessageHead;

/**
 * @author charles
 * @date 2017年11月3日
 */
public class TestAskAsync {

    public static void main(String[] args) throws Exception {
        CourierServer server = new CourierServer(8888);
        new Thread(new Runnable() {

            @Override
            public void run() {
                try {
                    server.start();
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        }).start();;

        Thread.sleep(1000);

        CourierClient client = new CourierClient();
        client.start();

        for (int i = 0; i < 5; i++) {
            Thread.sleep(1000);
            MessageHead head = MessageHead.buildMessageHead();
            Message message = new Message(head, ("askAsync" + i).getBytes());
            client.askAsync(message, "127.0.0.1", 8888, 5000, new ResponseCallback() {

                @Override
                public void onComplete(boolean success, Message response, Throwable cause) {
                    if (success) {
                        System.out.println("client receive(Async):" + response);
                    } else {
                        System.err.println("error:" + cause);
                    }
                }
            });
        }

        Thread.sleep(100000);

    }

}
