package name.chengchao.courier.protocol;

/**
 * @author charles
 * @date 2017年11月3日
 */
public class MessageHead {

    // 版本号
    private String v;

    public String getV() {
        return v;
    }

    public void setV(String v) {
        this.v = v;
    }

    @Override
    public String toString() {
        return "[v=" + v + "]";
    }

}
