package com.yhl.rpc.common;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * !!! 这里报错经常会没有错误栈，需要注意
 * Created by yuhongliang on 17-7-31.
 */
public class RpcMessageEncoder extends MessageToByteEncoder{
    private static final Logger LOGGER = LoggerFactory.getLogger(RpcMessageEncoder.class);

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] data = ProtoStuffSerialize.serialize(msg);
        out.writeInt(data.length);
        out.writeBytes(data);
        LOGGER.info("encode:msg.getClass() = " + msg.getClass() + ", data.length=" + data.length);
    }
}
