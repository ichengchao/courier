package name.chengchao.courier.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import name.chengchao.courier.utils.JsonUtils;

/**
 * 
 * 协议的消息结构体.协议的结构是: [消息总长度(int)|魔法数字(int)|消息头长度(int)|消息头(byte[])|消息体(byte[])]
 * 
 * @author charles
 * @date 2017年11月2日
 */
public class Message {

    public static final int MAGIC = 0xAABBCCDD;

    private MessageHead head;
    // 消息主体
    private byte[] body;

    public Message() {}

    public Message(MessageHead head, byte[] body) {
        super();
        this.head = head;
        this.body = body;
    }

    @Override
    public String toString() {
        return head + "[body=" + new String(body) + "]";
    }

    public MessageHead getHead() {
        return head;
    }

    public void setHead(MessageHead head) {
        this.head = head;
    }

    public byte[] getBody() {
        return body;
    }

    public void setBody(byte[] body) {
        this.body = body;
    }

    // 反序列化
    public static Message decode(ByteBuf frame) {
        int messageLength = frame.readInt();
        int magicNumber = frame.readInt();
        if (magicNumber != Message.MAGIC) {
            throw new RuntimeException("magic not equal!");
        }
        int headLength = frame.readInt();
        byte[] headBytes = new byte[headLength];
        frame.readBytes(headBytes);
        int bodyLength = messageLength - headLength - 8;
        byte[] body = null;
        if (bodyLength > 0) {
            body = new byte[bodyLength];
            frame.readBytes(body);
        }
        MessageHead head = JsonUtils.parseObject(headBytes, MessageHead.class);
        return new Message(head, body);
    }

    // 反序列化
    public ByteBuf encodeHeader() {
        byte[] headByte = JsonUtils.toByte(getHead());
        int length = headByte.length + 4 + 4;
        if (null != getBody()) {
            length += getBody().length;
        }
        ByteBuf byteBuf = Unpooled.buffer(length);
        byteBuf.writeInt(length);
        byteBuf.writeInt(Message.MAGIC);
        byteBuf.writeInt(headByte.length);
        byteBuf.writeBytes(headByte);
        return byteBuf;
    }

}
