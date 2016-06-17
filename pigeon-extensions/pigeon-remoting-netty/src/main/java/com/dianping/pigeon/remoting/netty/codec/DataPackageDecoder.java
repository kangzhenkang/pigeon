package com.dianping.pigeon.remoting.netty.codec;

import org.jboss.netty.buffer.ChannelBuffer;
import org.jboss.netty.channel.Channel;
import org.jboss.netty.channel.ChannelHandlerContext;
import org.jboss.netty.handler.codec.frame.FrameDecoder;

/**
 * @author qi.yin
 *         2016/06/16  下午2:38.
 */
public class DataPackageDecoder extends FrameDecoder {


    @Override
    protected Object decode(ChannelHandlerContext ctx, Channel channel, ChannelBuffer buffer)
            throws Exception {

        Object message = null;

        if (buffer.readableBytes() > 2) {

            byte[] headMsgs = new byte[2];

            buffer.getBytes(buffer.readerIndex(), headMsgs);

            if ((0x39 == headMsgs[0] && 0x3A == headMsgs[1])) {
                //old protocal
                message = decode0(buffer);

            } else if ((byte) 0xAB == headMsgs[0]
                    && (byte) 0xBA == headMsgs[1]) {
                //new protocal
                message = _decode0(buffer);

            } else {
                throw new IllegalArgumentException("Decode invalid message head:" +
                        headMsgs[0] + " " + headMsgs[1] + ", " + "message:" + buffer);
            }
        }

        return message;
    }

    protected Object decode0(ChannelBuffer buffer)
            throws Exception {

        DataPackage dataPackage = null;

        if (buffer.readableBytes() > CodecConstants.FRONT_LENGTH) {

            int totalLength = (int) buffer.getUnsignedInt(
                    buffer.readerIndex() +
                            CodecConstants.HEAD_LENGTH);

            int frameLength = totalLength + CodecConstants.FRONT_LENGTH;

            if (buffer.readableBytes() >= frameLength) {

                ChannelBuffer frame = buffer.slice(buffer.readerIndex(), frameLength);
                buffer.readerIndex(buffer.readerIndex() + frameLength);

                dataPackage = new DataPackage(frame, true);
            }

        }
        return dataPackage;
    }

    protected Object _decode0(ChannelBuffer buffer)
            throws Exception {
        DataPackage dataPackage = null;

        if (buffer.readableBytes() <= CodecConstants._FRONT_LENGTH) {
            return dataPackage;
        }

        int totalLength = (int) (buffer.getUnsignedInt(
                buffer.readerIndex() +
                        CodecConstants._HEAD_LENGTH));

        int frameLength = totalLength + CodecConstants._FRONT_LENGTH_;

        if (buffer.readableBytes() >= frameLength) {

            ChannelBuffer frame = buffer.slice(buffer.readerIndex(), frameLength);
            buffer.readerIndex(buffer.readerIndex() + frameLength);

            dataPackage = new DataPackage(frame, true);
        }

        return dataPackage;
    }


}