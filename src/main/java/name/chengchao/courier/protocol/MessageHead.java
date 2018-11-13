package name.chengchao.courier.protocol;

import name.chengchao.courier.model.BodyType;
import name.chengchao.courier.model.CompressType;

/**
 * 协议的结构是: 0xFFFFFFFF,顺序是从右到左,没有用到位置保留为扩展位 <br>
 * 1: version<br>
 * 2: 是否是request<br>
 * 3: 压缩类型
 * 4: 消息体类型
 * 
 * 
 * @author charles
 * @date 2017年11月3日
 */
public class MessageHead {

    private int version;

    private boolean isRequest;

    private CompressType compressType;

    private BodyType bodyType;

    public MessageHead() {
        this(1, false, CompressType.snappy, BodyType.json);
    }

    public MessageHead(boolean isRequest) {
        this(1, isRequest, CompressType.snappy, BodyType.json);
    }

    @Override
    public String toString() {
        return "[v=" + version + ", isRequest=" + isRequest + ", compressType=" + compressType
            + ", bodyType=" + bodyType + "]";
    }

    public MessageHead(int version, boolean isRequest, CompressType compressType, BodyType bodyType) {
        super();
        this.version = version;
        this.isRequest = isRequest;
        this.compressType = compressType;
        this.bodyType = bodyType;
    }

    public static MessageHead decodeHead(final int code) {
        MessageHead head = new MessageHead();
        int p_version = code & 0xF;
        int p_isRequest = (code >> 4) & 0xF;
        int p_compressType = (code >> 8) & 0xF;
        int p_bodyType = (code >> 12) & 0xF;
        head.setVersion(p_version);
        head.setRequest(p_isRequest == 1);
        head.setCompressType(CompressType.valueOfInt(p_compressType));
        head.setBodyType(BodyType.valueOfInt(p_bodyType));
        return head;
    }

    public int encodeHead() {
        int code = 0x00000000;
        code = code | (this.version & 0xF);
        code = code | ((this.isRequest ? 1 : 0) & 0xF) << 4;
        code = code | (this.compressType.getValue() & 0xF) << 8;
        code = code | (this.bodyType.getValue() & 0xF) << 12;
        return code;
    }

    public static void main(String[] args) {
        int code = 0x00001111;
        System.out.println(code);
        MessageHead head = MessageHead.decodeHead(code);
        System.out.println(head);
        System.out.println(head.encodeHead());
        System.out.println(Integer.toHexString(head.encodeHead()));

    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public boolean isRequest() {
        return isRequest;
    }

    public void setRequest(boolean isRequest) {
        this.isRequest = isRequest;
    }

    public CompressType getCompressType() {
        return compressType;
    }

    public void setCompressType(CompressType compressType) {
        this.compressType = compressType;
    }

    public BodyType getBodyType() {
        return bodyType;
    }

    public void setBodyType(BodyType bodyType) {
        this.bodyType = bodyType;
    }

}
