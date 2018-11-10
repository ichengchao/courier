package name.chengchao.courier.test;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import name.chengchao.courier.protocol.Message;
import name.chengchao.courier.protocol.MessageHead;

public class TestSerialize {

    final static byte[] body_100KB = new byte[100000];
    final static byte[] body_1MB = new byte[1000000];

    public static void main(String[] args) throws Exception {
        long start = System.currentTimeMillis();
        // encodePerf();
        // decodePerf();
        allocPerf();
        long end = System.currentTimeMillis();
        System.out.println("time(ms):" + (end - start));
    }

    public static void allocPerf() {
        for (int i = 0; i < 200000; i++) {
            byte[] headBytes = new byte[1000000];
            // Unpooled.buffer(1000034);
            if (i % 10000 == 0) {
                System.out.println(i);
            }
        }
    }

    public static void encodePerf() {

        MessageHead head = MessageHead.buildMessageHead();
        Message message = new Message(head, body_100KB);
        for (int i = 0; i < 2000000; i++) {
            message.encodeHeader();
            if (i % 10000 == 0) {
                System.out.println(i);
            }
        }
        System.out.println("done");
    }

    public static void decodePerf() {

        MessageHead head = MessageHead.buildMessageHead();
        Message message = new Message(head, body_100KB);
        ByteBuf headBuf = message.encodeHeader();
        ByteBuf msg = Unpooled.buffer(headBuf.array().length + message.getBody().length);
        msg.writeBytes(headBuf.array());
        msg.writeBytes(message.getBody());
        for (int i = 0; i < 2000000; i++) {
            Message.decode(msg);
            msg.resetReaderIndex();
            if (i % 10000 == 0) {
                System.out.println(i);
            }
        }
        System.out.println("done");
    }

}
