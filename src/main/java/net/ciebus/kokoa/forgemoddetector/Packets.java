package net.ciebus.kokoa.forgemoddetector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import io.netty.buffer.ByteBuf;
import org.bukkit.entity.Player;

public class Packets extends PacketAdapter {
    private CorePackets handler;

    public Packets(ForgeModDetector plugin) {
        super(plugin, PacketType.Login.Client.START, PacketType.Login.Client.CUSTOM_PAYLOAD);
        this.handler = new PacketHandlers(plugin.getCoremods());
    }

    public void onPacketReceiving(PacketEvent event) {
        event.setCancelled(true);
        Player player = event.getPlayer();
        String address = player.getAddress().toString();
        if (event.getPacketType() != PacketType.Login.Client.START) {
            ByteBuf buf = (ByteBuf)event.getPacket().getModifier().read(1);
            byte[] bytes = null;
            if (buf != null) {
                bytes = new byte[buf.readableBytes()];
                buf.readBytes(bytes);
            }

            this.handler.onLoginPayload(player, address, (Integer)event.getPacket().getModifier().read(0), bytes);
        } else {
            this.handler.onLoginStart(player, address, ((WrappedGameProfile)event.getPacket().getGameProfiles().read(0)).getName());
        }
    }
}
