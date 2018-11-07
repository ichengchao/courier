package name.chengchao.courier;

import name.chengchao.courier.protocol.Message;

/**
 * @author charles
 * @date 2017年11月20日
 */
public interface ResponseCallback {

    void onComplete(boolean success, Message response, Throwable cause);

}
