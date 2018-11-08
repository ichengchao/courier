package name.chengchao.courier.test;

import name.chengchao.courier.CourierClient;
import name.chengchao.courier.CourierServer;
import name.chengchao.courier.handler.CustomMessageHandler;
import name.chengchao.courier.protocol.Message;
import name.chengchao.courier.protocol.MessageHead;

/**
 * @author charles
 * @date 2017年11月3日
 */
public class TestAskSync {

    public static void main(String[] args) throws Exception {
        new CourierServer(8888, new CustomMessageHandler() {

            @Override
            public Message handle(Message request) {
                MessageHead head = request.getHead();
                head.setReq(false);
                Message response = new Message(head, ("sync callback").getBytes());

                return response;
            }
        }).serve();

        Thread.sleep(3000);

        CourierClient client = new CourierClient();
        client.start();

        for (int i = 0; i < 3; i++) {
            Thread.sleep(1000);
            MessageHead head = MessageHead.buildMessageHead();
            Message message = new Message(head, ("askSync" + i).getBytes());
            System.out.println("client send(sync):" + message);
            Message response = client.ask(message, "127.0.0.1", 8888, 3000);
            System.out.println("client receive(sync):" + response);
        }

        // Thread.sleep(20000);
        // MessageHead head = MessageHead.buildMessageHead();
        // Message message = new Message(head, ("askSync").getBytes());
        // System.out.println("client send(syncssssssssss):" + message);
        // Message response = client.ask(message, "127.0.0.1", 8888, 3000);
        //
        // System.out.println("client receive(sync):" + response);
        // Thread.sleep(100000);

    }

}
