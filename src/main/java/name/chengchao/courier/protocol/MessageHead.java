package name.chengchao.courier.protocol;

import name.chengchao.courier.utils.UuidUtils;

/**
 * @author charles
 * @date 2017年11月3日
 */
public class MessageHead {

    // version
    private String v = "1";

    // sequence
    private int s;

    // is request
    private boolean req = true;

    public static MessageHead buildMessageHead() {
        MessageHead head = new MessageHead();
        head.setS(UuidUtils.getSequence());
        return head;
    }

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    public int getS() {
        return s;
    }

    public void setS(int s) {
        this.s = s;
    }

    public boolean isReq() {
        return req;
    }

    public void setReq(boolean req) {
        this.req = req;
    }

    @Override
    public String toString() {
        return "MessageHead [v=" + v + ", s=" + s + ", req=" + req + "]";
    }

}
