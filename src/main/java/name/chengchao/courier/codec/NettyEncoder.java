package name.chengchao.courier.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import name.chengchao.courier.protocol.Message;
import name.chengchao.courier.utils.Assert;
import name.chengchao.courier.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
            byte[] headByte = JsonUtils.toByte(message.getHead());
            out.writeInt(headByte.length + message.getBody().length + 4 + 4);
            out.writeInt(Message.MAGIC);
            out.writeInt(headByte.length);
            out.writeBytes(headByte);
            out.writeBytes(message.getBody());
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ctx.channel().close();
        }
    }

}
