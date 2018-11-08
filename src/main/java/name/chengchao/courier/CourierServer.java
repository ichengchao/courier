package name.chengchao.courier;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.timeout.IdleStateHandler;
import name.chengchao.courier.codec.NettyDecoder;
import name.chengchao.courier.codec.NettyEncoder;
import name.chengchao.courier.context.ContextHolder;
import name.chengchao.courier.handler.CustomMessageHandler;
import name.chengchao.courier.handler.MessageHandler;

/**
 * @author charles
 * @date 2017年11月3日
 */
public class CourierServer {

    private static final Logger logger = LoggerFactory.getLogger(CourierServer.class);

    private int port;

    private CustomMessageHandler customMessageHandler;

    public CourierServer(int port) {
        this.port = port;
    }

    public CourierServer(int port, CustomMessageHandler customMessageHandler) {
        this.port = port;
        this.customMessageHandler = customMessageHandler;
    }

    public CourierServer serve() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    start();
                } catch (Exception e) {
                    logger.error("start courier server with error:", e);
                }
            }
        }, "courier-remoting-server").start();
        return this;
    }

    public void start() throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup(1);
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            ServerBootstrap server = new ServerBootstrap();
            server.group(bossGroup, workerGroup).channel(NioServerSocketChannel.class);
            server.childHandler(new ChannelInitializer<SocketChannel>() {
                @Override
                public void initChannel(SocketChannel ch) throws Exception {
                    ch.pipeline().addLast(new NettyDecoder());
                    ch.pipeline().addLast(new NettyEncoder());
                    ch.pipeline().addLast(new IdleStateHandler(0, 0, ContextHolder.IDLE_TIMEOUT_SECONDS));
                    ch.pipeline().addLast(new MessageHandler(customMessageHandler));
                }
            });
            server.option(ChannelOption.SO_BACKLOG, 1024);
            server.childOption(ChannelOption.TCP_NODELAY, true);
            server.childOption(ChannelOption.SO_KEEPALIVE, true);
            ChannelFuture f = server.bind(port).sync();
            logger.info("server start up,port:" + port);
            f.channel().closeFuture().sync();
        } finally {
            workerGroup.shutdownGracefully();
            bossGroup.shutdownGracefully();
        }
    }

}
