package name.chengchao.courier.protocol;

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

    public static void main(String[] args) {
        System.out.println(MAGIC);
    }

}
