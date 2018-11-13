package name.chengchao.courier.test;

import name.chengchao.courier.CourierClient;
import name.chengchao.courier.CourierServer;
import name.chengchao.courier.ResponseCallback;
import name.chengchao.courier.handler.CustomMessageHandler;
import name.chengchao.courier.protocol.Message;

/**
 * @author charles
 * @date 2017年11月3日
 */
public class TestAskAsync {

    public static void main(String[] args) throws Exception {
        new CourierServer(8888, new CustomMessageHandler() {

            @Override
            public Message handle(Message request) {
                Message response = Message.buildResponseMsg(request.getSequence(), ("Async callback").getBytes());
                return response;
            }
        }).serve();

        Thread.sleep(1000);

        CourierClient client = new CourierClient();
        client.start();

        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            Message message = Message.buildRequestMsg(("askAsync" + i).getBytes());
            System.out.println("client send(Async):" + message);
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
