package name.chengchao.courier;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import name.chengchao.courier.codec.NettyDecoder;
import name.chengchao.courier.codec.NettyEncoder;
import name.chengchao.courier.protocol.Message;
import name.chengchao.courier.protocol.MessageHead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * @author charles
 * @date 2017年11月3日
 */
public class CourierServer {

    private static final Logger logger = LoggerFactory.getLogger(CourierServer.class);

    private int port;

    public CourierServer(int port) {
        this.port = port;
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
                    ch.pipeline().addLast(new NettyServerHandler());
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

    class NettyServerHandler extends SimpleChannelInboundHandler<Message> {

        @Override
        protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {
            System.out.println("server receive:" + msg.toString());
            MessageHead head = msg.getHead();
            head.setReq(false);
            Message message = new Message(head, ("callback").getBytes());
            ctx.channel().writeAndFlush(message);
        }
    }
}
