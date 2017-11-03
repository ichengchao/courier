package name.chengchao.courier;

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
public class CourierClient {
    private static final Logger logger = LoggerFactory.getLogger(CourierServer.class);

    private final Bootstrap bootstrap = new Bootstrap();
    private final EventLoopGroup workerGroup = new NioEventLoopGroup();

    public static final long ConnetTimeout = 3000;

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

    public void tell(Message message) {
        ChannelFuture channelFuture = bootstrap.connect("127.0.0.1", 8888);
        if (channelFuture.awaitUninterruptibly(ConnetTimeout)) {
            Channel channel = channelFuture.channel();
            if (null != channel && channel.isActive()) {
                channel.writeAndFlush(message);
            } else {
                logger.error(channelFuture.cause().getMessage());
            }
        } else {
            logger.error("connect timeout[{}] to [{}]", "127.0.0.1:8888", ConnetTimeout);
        }
    }

    public Message ask() {
        return null;
    }

    public void askAsync() {

    }

    class NettyClientHandler extends SimpleChannelInboundHandler<Message> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
            System.out.println("client receive:" + msg.toString());
        }
    }

}
