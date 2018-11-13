package name.chengchao.courier.protocol;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import name.chengchao.courier.utils.UuidUtils;

/**
 * 
 * 协议的消息结构体.协议的结构是: [消息总长度(int)|魔法数字(int)|序列号(int)|消息头(int)|消息体(byte[])]
 * 
 * @author charles
 * @date 2017年11月2日
 */
public class Message {

    public static final int MAGIC = 0xAABBCCDD;

    private int sequence;

    private MessageHead head;
    // 消息主体
    private byte[] body;

    public static Message buildRequestMsg(byte[] body) {
        Message message = new Message();
        MessageHead head = new MessageHead(true);
        message.setHead(head);
        message.setSequence(UuidUtils.getSequence());
        message.setBody(body);
        return message;
    }

    public static Message buildResponseMsg(int sequence, byte[] body) {
        Message message = new Message();
        MessageHead head = new MessageHead(false);
        message.setHead(head);
        message.setSequence(sequence);
        message.setBody(body);
        return message;
    }

    @Override
    public String toString() {
        return "[sequence=" + sequence + ", head=" + head + ", body=" + new String(body) + "]";
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

    public int getSequence() {
        return sequence;
    }

    public void setSequence(int sequence) {
        this.sequence = sequence;
    }

    // 反序列化
    public static Message decode(ByteBuf frame) {
        int messageLength = frame.readInt();
        int magicNumber = frame.readInt();
        if (magicNumber != Message.MAGIC) {
            throw new RuntimeException("magic not equal!");
        }
        int sequence = frame.readInt();
        int headCode = frame.readInt();
        MessageHead head = MessageHead.decodeHead(headCode);
        int bodyLength = messageLength - 12;
        byte[] body = null;
        if (bodyLength > 0) {
            body = new byte[bodyLength];
            frame.readBytes(body);
        }
        Message msg = new Message();
        msg.setHead(head);
        msg.setBody(body);
        msg.setSequence(sequence);
        return msg;
    }

    // 反序列化
    public ByteBuf encodeHeader() {
        int length = 12;
        if (null != getBody()) {
            length += getBody().length;
        }
        ByteBuf byteBuf = Unpooled.buffer(length);
        byteBuf.writeInt(length);
        byteBuf.writeInt(Message.MAGIC);
        byteBuf.writeInt(this.sequence);
        byteBuf.writeInt(this.head.encodeHead());
        return byteBuf;
    }

}
