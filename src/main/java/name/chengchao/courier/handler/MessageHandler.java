package name.chengchao.courier.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.timeout.IdleState;
import io.netty.handler.timeout.IdleStateEvent;
import name.chengchao.courier.ResponseFuture;
import name.chengchao.courier.context.ContextHolder;
import name.chengchao.courier.protocol.Message;
import name.chengchao.courier.utils.RemotingUtils;

public class MessageHandler extends SimpleChannelInboundHandler<Message> {

    private static Logger logger = LoggerFactory.getLogger(MessageHandler.class);

    private CustomMessageHandler customMessageHandler;

    public MessageHandler(CustomMessageHandler customMessageHandler) {
        this.customMessageHandler = customMessageHandler;
    }

    public MessageHandler() {}

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, Message msg) throws Exception {

        if (msg.getHead().isReq()) {
            // server处理
            if (null != customMessageHandler) {
                Message response = customMessageHandler.handle(msg);
                if (null != response) {
                    ctx.channel().writeAndFlush(response);
                }
            }
        } else {
            // client处理
            final ResponseFuture responseFuture = ContextHolder.callbackMap.get(msg.getHead().getS());
            if (null != responseFuture) {
                responseFuture.receiveResponse(msg);
            }
        }
    }

    // 处理idle的事件
    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {
        if (evt instanceof IdleStateEvent) {
            IdleStateEvent event = (IdleStateEvent)evt;
            if (event.state().equals(IdleState.ALL_IDLE)) {
                final String remoteAddress = RemotingUtils.parseChannelRemoteAddr(ctx.channel());
                logger.warn("NETTY PIPELINE: IDLE exception [{}]", remoteAddress);
                RemotingUtils.closeChannel(ctx.channel());
            }
        }
        super.userEventTriggered(ctx, evt);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        final String remoteAddress = RemotingUtils.parseChannelRemoteAddr(ctx.channel());
        logger.warn("NETTY PIPELINE: exceptionCaught -> " + remoteAddress, cause);
        RemotingUtils.closeChannel(ctx.channel());
    }

}
