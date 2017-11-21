package name.chengchao.courier.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import name.chengchao.courier.protocol.Message;
import name.chengchao.courier.protocol.MessageHead;
import name.chengchao.courier.utils.JsonUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 基于长度的decoder
 * 
 * @author charles
 * @date 2017年11月3日
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {

    public static final Logger logger = LoggerFactory.getLogger(NettyDecoder.class);

    // 最大长度
    public static final int MAX_LENGTH = 32 * 1024 * 1024;

    public NettyDecoder() {
        super(MAX_LENGTH, 0, 4);
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = null;
        try {
            frame = (ByteBuf)super.decode(ctx, in);
            if (null == frame) {
                return null;
            }
            int messageLength = frame.readInt();
            int magicNumber = frame.readInt();
            if (magicNumber != Message.MAGIC) {
                throw new RuntimeException("magic not equal!");
            }
            int headLength = frame.readInt();
            byte[] headBytes = new byte[headLength];
            byte[] body = new byte[messageLength - headLength - 8];
            frame.readBytes(headBytes);
            frame.readBytes(body);
            MessageHead head = JsonUtils.parseObject(headBytes, MessageHead.class);
            return new Message(head, body);
        } catch (Exception e) {
            logger.error(e.getMessage(), e);
            ctx.channel().close();
        } finally {
            if (null != frame) {
                frame.release();
            }
        }
        return null;
    }

}
