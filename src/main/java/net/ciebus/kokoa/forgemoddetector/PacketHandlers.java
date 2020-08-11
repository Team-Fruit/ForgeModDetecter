package net.ciebus.kokoa.forgemoddetector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.reflect.StructureModifier;
import com.comphenix.protocol.wrappers.MinecraftKey;
import com.mojang.authlib.GameProfile;
import io.netty.buffer.ByteBuf;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import io.netty.buffer.Unpooled;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.UUID;

public class PacketHandlers extends CorePackets {
    private Constructor constructor;

    public PacketHandlers(Core mods) {
        super(mods);

        try {
            this.constructor = Class.forName("net.minecraft.server." + Bukkit.getServer().getClass().getName().split("\\.")[3] + ".PacketDataSerializer").getConstructor(ByteBuf.class);
        } catch (Exception var3) {
            var3.printStackTrace();
        }

    }

    public void sendHandshakeStart(Object player) {
        PacketContainer packet1 = new PacketContainer(PacketType.Login.Server.CUSTOM_PAYLOAD);
        StructureModifier<Object> mod = packet1.getModifier();
        mod.write(0, this.getHandshakeInitId());
        packet1.getMinecraftKeys().write(0, new MinecraftKey("fml", "handshake"));

        try {
            packet1.getModifier().write(2, this.createSerializer(this.getMods().getFmlHandshake()));
            ProtocolLibrary.getProtocolManager().sendServerPacket((Player)player, packet1);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    public void sendRegistries(Object player) {
        PacketContainer packet1 = new PacketContainer(PacketType.Login.Server.CUSTOM_PAYLOAD);
        StructureModifier<Object> mod = packet1.getModifier();
        mod.write(0, this.getHandshakeRegistryId());
        packet1.getMinecraftKeys().write(0, new MinecraftKey("fml", "handshake"));

        try {
            packet1.getModifier().write(2, this.createSerializer(this.getMods().getFmlRegistries()));
            ProtocolLibrary.getProtocolManager().sendServerPacket((Player)player, packet1);
        } catch (Exception var5) {
            var5.printStackTrace();
        }

    }

    private Object createSerializer(byte[] bytes) throws IllegalAccessException, InvocationTargetException, InstantiationException {
        return this.constructor.newInstance(Unpooled.wrappedBuffer(bytes));
    }

    public void sendFinishLogin(Object player, String name) {
        PacketContainer packet = new PacketContainer(PacketType.Login.Client.START);
        packet.getModifier().write(0, new GameProfile((UUID)null, name));

        try {
            ProtocolLibrary.getProtocolManager().recieveClientPacket((Player)player, packet, false);
        } catch (InvocationTargetException | IllegalAccessException var5) {
            var5.printStackTrace();
        }

    }
}