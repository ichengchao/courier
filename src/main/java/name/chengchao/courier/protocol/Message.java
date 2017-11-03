package name.chengchao.courier.protocol;

/**
 * 
 * 协议的消息结构体
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
        return head + "[bodySize=" + body.length + "]";
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
