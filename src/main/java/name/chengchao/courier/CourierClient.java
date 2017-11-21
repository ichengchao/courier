package name.chengchao.courier;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import name.chengchao.courier.codec.NettyDecoder;
import name.chengchao.courier.codec.NettyEncoder;
import name.chengchao.courier.protocol.Message;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author charles
 * @date 2017年11月3日
 */
public class CourierClient extends CourierBase {
    private static final Logger logger = LoggerFactory.getLogger(CourierServer.class);

    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    private ExecutorService commonExecutor;
    private ScheduledExecutorService scheduledExecutorService = Executors.newScheduledThreadPool(2);

    public static final long ConnetTimeout = 3000;

    public CourierClient() {
        super();
        System.out.println("client start!!");
        commonExecutor = Executors.newFixedThreadPool(4);
    }

    public void start() {
        bootstrap.group(workerGroup).channel(NioSocketChannel.class);
        bootstrap.handler(new ChannelInitializer<SocketChannel>() {
            @Override
            public void initChannel(SocketChannel ch) throws Exception {
                ch.pipeline().addLast(new NettyDecoder());
                ch.pipeline().addLast(new NettyEncoder());
                ch.pipeline().addLast(new NettyClientHandler());
            }
        });
        bootstrap.option(ChannelOption.TCP_NODELAY, true);
        bootstrap.option(ChannelOption.SO_KEEPALIVE, true);
    }

    public void tell(Message message, String ip, int port) {
        Channel channel = getOrCreateChannelFuture(ip, port);
        if (null != channel && channel.isActive()) {
            channel.writeAndFlush(message);
        }
    }

    private Channel getOrCreateChannelFuture(String ip, int port) {
        String channelKey = ip + ":" + port;
        Channel channel = getChannelMap().get(channelKey);
        if (channel == null) {
            ChannelFuture channelFuture = bootstrap.connect(ip, port);
            if (channelFuture.awaitUninterruptibly(ConnetTimeout)) {
                channel = channelFuture.channel();
                if (null != channel && channel.isActive()) {
                    getChannelMap().put(channelKey, channel);
                    return channel;
                } else {
                    logger.error(channelFuture.cause().getMessage());
                }
            }
        }
        return channel;
    }

    public Message ask(Message message, String ip, int port, int timeoutMS) throws InterruptedException {
        ResponseFuture responseFuture = new ResponseFuture(message.getHead().getS(), null, timeoutMS);
        getCallbackMap().put(message.getHead().getS(), responseFuture);
        tell(message, ip, port);
        responseFuture.getSyncLockLatch().await(timeoutMS, TimeUnit.MILLISECONDS);
        return responseFuture.getResponse();
    }

    public void askAsync(Message message, String ip, int port, int timeoutMS, ResponseCallback responseCallback) {
        ResponseFuture responseFuture = new ResponseFuture(message.getHead().getS(), responseCallback, timeoutMS);
        getCallbackMap().put(message.getHead().getS(), responseFuture);
        tell(message, ip, port);
        responseFuture.invokeTimeoutCount(scheduledExecutorService, commonExecutor, getCallbackMap());
    }

    class NettyClientHandler extends SimpleChannelInboundHandler<Message> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
            if (!msg.getHead().isReq()) {
                final ResponseFuture responseFuture = getCallbackMap().get(msg.getHead().getS());
                if (null != responseFuture) {
                    responseFuture.responseDone(msg);
                    responseFuture.doCallback(commonExecutor, getCallbackMap());
                }
            }
        }
    }

}
