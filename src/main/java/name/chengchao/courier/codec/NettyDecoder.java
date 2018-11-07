package name.chengchao.courier.codec;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import name.chengchao.courier.protocol.Message;
import name.chengchao.courier.utils.RemotingUtils;

/**
 * 基于长度的decoder
 * 
 * @author charles
 * @date 2017年11月3日
 */
public class NettyDecoder extends LengthFieldBasedFrameDecoder {

    public static final Logger logger = LoggerFactory.getLogger(NettyDecoder.class);

    // 最大长度32MB
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
            return Message.decode(frame);
        } catch (Exception e) {
            logger.error("decode exception, " + RemotingUtils.parseChannelRemoteAddr(ctx.channel()), e);
            RemotingUtils.closeChannel(ctx.channel());
        } finally {
            if (null != frame) {
                frame.release();
            }
        }
        return null;
    }

}
