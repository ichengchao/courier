package name.chengchao.courier.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import name.chengchao.courier.protocol.Message;
import name.chengchao.courier.utils.Assert;
import name.chengchao.courier.utils.RemotingUtils;

/**
 * @author charles
 * @date 2017年11月3日
 */
public class NettyEncoder extends MessageToByteEncoder<Message> {

    private static final Logger logger = LoggerFactory.getLogger(NettyEncoder.class);

    @Override
    public void encode(ChannelHandlerContext ctx, Message message, ByteBuf out) throws Exception {
        try {
            Assert.notNull(message, "message can not be null!");
            ByteBuf header = message.encodeHeader();
            out.writeBytes(header);
            byte[] body = message.getBody();
            if (body != null) {
                out.writeBytes(body);
            }
        } catch (Exception e) {
            logger.error("encode exception, " + RemotingUtils.parseChannelRemoteAddr(ctx.channel()), e);
            if (message != null) {
                logger.error(message.toString());
            }
            RemotingUtils.closeChannel(ctx.channel());
        }
    }

}
