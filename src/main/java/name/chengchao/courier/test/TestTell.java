package name.chengchao.courier.test;

import name.chengchao.courier.CourierClient;
import name.chengchao.courier.CourierServer;
import name.chengchao.courier.handler.CustomMessageHandler;
import name.chengchao.courier.protocol.Message;

/**
 * @author charles
 * @date 2017年11月3日
 */
public class TestTell {

    public static void main(String[] args) throws Exception {
        new CourierServer(8888, new CustomMessageHandler() {

            @Override
            public Message handle(Message request) {
                System.out.println("[server]:" + request);
                return null;
            }
        }).serve();
        Thread.sleep(3000);

        CourierClient client = new CourierClient();
        client.start();

        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            Message message = Message.buildRequestMsg(("tell" + i).getBytes());
            client.tell(message, "127.0.0.1", 8888);
        }

    }

}
