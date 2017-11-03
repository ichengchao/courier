package name.chengchao.courier;

import name.chengchao.courier.protocol.Message;
import name.chengchao.courier.protocol.MessageHead;

/**
 * @author charles
 * @date 2017年11月3日
 */
public class MyTest {

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

        Thread.sleep(3000);

        CourierClient client = new CourierClient();
        client.start();

        for (int i = 0; i < 100; i++) {
            Thread.sleep(1000);
            MessageHead head = new MessageHead();
            head.setV("Version1");
            Message message = new Message(head, ("test" + i).getBytes());
            client.tell(message);
        }

    }

}
