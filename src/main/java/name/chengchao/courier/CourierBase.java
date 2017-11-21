package name.chengchao.courier;

import java.util.concurrent.ConcurrentHashMap;

import io.netty.channel.Channel;

/**
 * @author charles
 * @date 2017年11月21日
 */
public class CourierBase {

    private ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<Integer, ResponseFuture> callbackMap = new ConcurrentHashMap<>();

    public ConcurrentHashMap<Integer, ResponseFuture> getCallbackMap() {
        return callbackMap;
    }

    public ConcurrentHashMap<String, Channel> getChannelMap() {
        return channelMap;
    }

}
