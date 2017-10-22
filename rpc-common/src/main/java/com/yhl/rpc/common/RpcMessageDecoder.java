package com.yhl.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Created by yuhongliang on 17-7-31.
 */
public class RpcMessageDecoder extends LengthFieldBasedFrameDecoder {
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcMessageDecoder.class);

    private Class<?> messageClass;

    public RpcMessageDecoder(int maxFrameLength, int lengthFieldOffset, int lengthFieldLength, Class<?> messageClass) {
        super(maxFrameLength, lengthFieldOffset, lengthFieldLength);
        this.messageClass = messageClass;
    }

    @Override
    protected Object decode(ChannelHandlerContext ctx, ByteBuf in) throws Exception {
        ByteBuf frame = (ByteBuf) super.decode(ctx, in);
        LOGGER.info("frame is:" + frame);

        if (frame == null) {
            return null;
        }
        try {
            int length = frame.readInt();
            LOGGER.info("length:" + length);
            byte[] data = new byte[length];
            if (frame.readableBytes() >= length) {
                frame.readBytes(data);
                return ProtoStuffSerialize.deserialize(data, messageClass);
            }
            return new Object();
        } finally {
            frame.release();
        }
    }
}
