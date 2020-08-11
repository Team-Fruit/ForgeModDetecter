package net.ciebus.kokoa.forgemoddetector;

import io.netty.buffer.ByteBuf;

import io.netty.buffer.Unpooled;
import io.netty.handler.codec.DecoderException;
import io.netty.handler.codec.EncoderException;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class Utils {
    public Utils() {
    }

    public static int getInt(ByteBuf buf) {
        int i = 0;
        int j = 0;

        byte b0;
        do {
            b0 = buf.readByte();
            i |= (b0 & 127) << j++ * 7;
            if (j > 5) {
                throw new RuntimeException("VarInt too big");
            }
        } while((b0 & 128) == 128);

        return i;
    }

    public static String getString(ByteBuf buf) {
        int j = getInt(buf);
        int size = 256;
        if (j > size * 4) {
            throw new DecoderException("The received encoded string buffer length is longer than maximum allowed (" + j + " > " + size * 4 + ")");
        } else if (j < 0) {
            throw new DecoderException("The received encoded string buffer length is less than zero! Weird string!");
        } else {
            String s = buf.toString(buf.readerIndex(), j, StandardCharsets.UTF_8);
            buf.readerIndex(buf.readerIndex() + j);
            if (s.length() > size) {
                throw new DecoderException("The received string length is longer than maximum allowed (" + j + " > " + size + ")");
            } else {
                return s;
            }
        }
    }

    public static void writeInt(DataOutputStream output, int value) throws IOException {
        while((value & -128) != 0) {
            output.writeByte(value & 127 | 128);
            value >>>= 7;
        }

        output.writeByte(value);
    }

    public static void writeString(DataOutputStream output, String s) throws IOException {
        byte[] abyte = s.getBytes(StandardCharsets.UTF_8);
        int i = 32767;
        if (abyte.length > i) {
            throw new EncoderException("String too big (was " + abyte.length + " bytes encoded, max " + i + ")");
        } else {
            writeInt(output, abyte.length);
            output.write(abyte);
        }
    }

    public static byte[] getModsPacket(List<String> mods, boolean useNewData) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);
        writeInt(output, 2);
        writeInt(output, mods.size());
        Iterator var4 = mods.iterator();

        while(var4.hasNext()) {
            String s = (String)var4.next();
            writeString(output, s);
            if (!useNewData) {
                writeString(output, "???");
            }
        }

        return stream.toByteArray();
    }

    public static List<String> getMods(byte[] data, boolean useNewData) {
        ByteBuf buf = Unpooled.wrappedBuffer(data);
        int packetId = getInt(buf);
        if (packetId != 2) {
            return null;
        } else {
            List<String> mods = new ArrayList();
            int count = getInt(buf);

            for(int i = 0; i < count; ++i) {
                mods.add(getString(buf));
                if (!useNewData) {
                    getString(buf);
                }
            }

            return mods;
        }
    }
}
