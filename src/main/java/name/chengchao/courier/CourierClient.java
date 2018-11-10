package name.chengchao.courier;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import name.chengchao.courier.codec.NettyDecoder;
import name.chengchao.courier.codec.NettyEncoder;
import name.chengchao.courier.context.ContextHolder;
import name.chengchao.courier.handler.MessageHandler;
import name.chengchao.courier.protocol.Message;

/**
 * @author charles
 * @date 2017年11月3日
 */
public class CourierClient {

    private AtomicInteger msgCount = new AtomicInteger(0);
    private AtomicInteger msgSuccessCount = new AtomicInteger(0);
    private AtomicInteger msgErrorCount = new AtomicInteger(0);
    private Throwable lastCause = null;

    private static final Logger logger = LoggerFactory.getLogger(CourierServer.class);

    private ConcurrentHashMap<String, Channel> channelMap = new ConcurrentHashMap<>();

    private ConcurrentHashMap<String, Lock> lockMap = new ConcurrentHashMap<>();

    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public CourierClient() {}

    public void start() {
        bootstrap.group(workerGroup).channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new NettyDecoder());
                ch.pipeline().addLast(new NettyEncoder());
                ch.pipeline().addLast(new IdleStateHandler(0, 0, ContextHolder.IDLE_TIMEOUT_SECONDS));
                ch.pipeline().addLast(new MessageHandler());
            }
        });
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    }

    synchronized Lock getLock(String target) {
        Lock lock = lockMap.get(target);
        if (null == lock) {
            lock = new ReentrantLock();
            lockMap.put(target, lock);
        }
        return lock;
    }

    private Channel getOrCreateChannelFuture(String ip, int port) {
        String channelKey = ip + ":" + port;
        Channel channel = channelMap.get(channelKey);
        if (channel != null && channel.isActive()) {
            return channel;
        } else {
            try {
                if (getLock(channelKey).tryLock(ContextHolder.LOCK_CREATE_TIMEOUT_MILLIS, TimeUnit.MILLISECONDS)) {
                    try {
                        channel = channelMap.get(channelKey);
                        if (channel != null && channel.isActive()) {
                            return channel;
                        } else {
                            channelMap.remove(channelKey);
                        }

                        ChannelFuture channelFuture = bootstrap.connect(ip, port);
                        if (channelFuture.awaitUninterruptibly(ContextHolder.CONNECT_TIMEOUT_MILLIS)) {
                            channel = channelFuture.channel();
                            if (null != channel && channel.isActive()) {
                                channelMap.put(channelKey, channel);
                                return channel;
                            } else {
                                logger.error(channelFuture.cause().getMessage());
                            }
                        }

                    } finally {
                        getLock(channelKey).unlock();
                    }
                } else {
                    logger.error("createChannel: try to lock channelMap, but timeout, {}ms",
                        ContextHolder.LOCK_CREATE_TIMEOUT_MILLIS);
                }

            } catch (Exception e) {
                logger.error("createChannel: create channel exception", e);
            }
        }
        throw new RuntimeException("can not connect to ip:" + ip + ":" + port);

    }

    public void tell(Message message, String ip, int port) {
        Channel channel = getOrCreateChannelFuture(ip, port);
        if (null != channel && channel.isActive()) {

            msgCount.incrementAndGet();
            channel.writeAndFlush(message).addListener(new ChannelFutureListener() {

                @Override
                public void operationComplete(ChannelFuture future) throws Exception {
                    if (future.isSuccess()) {
                        msgSuccessCount.incrementAndGet();
                    } else {
                        msgErrorCount.incrementAndGet();
                        if (future.cause() != null) {
                            lastCause = future.cause();
                        }
                    }
                }
            });
        }
    }

    public Message ask(Message message, String ip, int port, int timeoutMS) {
        ResponseFuture responseFuture = new ResponseFuture(message.getHead().getS(), null, timeoutMS);
        ContextHolder.callbackMap.put(message.getHead().getS(), responseFuture);
        tell(message, ip, port);
        return responseFuture.getSyncResult();
    }

    public void askAsync(Message message, String ip, int port, int timeoutMS, ResponseCallback responseCallback) {
        ResponseFuture responseFuture = new ResponseFuture(message.getHead().getS(), responseCallback, timeoutMS);
        ContextHolder.callbackMap.put(message.getHead().getS(), responseFuture);
        tell(message, ip, port);
        responseFuture.invokeTimeoutCount();
    }

    public int getMsgCount() {
        return msgCount.get();
    }

    public int getMsgSuccessCount() {
        return msgSuccessCount.get();
    }

    public int getMsgErrorCount() {
        return msgErrorCount.get();
    }

    public Throwable getLastCause() {
        return lastCause;
    }

}
