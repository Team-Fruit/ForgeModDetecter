package net.ciebus.kokoa.forgemoddetector;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.metadata.MetadataValue;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.List;

public class ForgeModDetector extends JavaPlugin implements PluginMessageListener, Listener {
    private final boolean usePacketListener;
    private final Configs config;
    private final Core coremods;
    private final boolean bungee;

    public ForgeModDetector() {
        this.usePacketListener = PacketType.Login.Server.CUSTOM_PAYLOAD.isSupported();
        this.config = new Configs();
        this.coremods = new Core(this.config);
        this.bungee = !Bukkit.getOnlineMode();
    }

    public void onEnable() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(this, "fabric:registry/sync");

        Commands cmd = new Commands(this);
        this.getCommand("mods").setExecutor(cmd);
        this.getCommand("mods").setTabCompleter(cmd);
        Bukkit.getPluginManager().registerEvents(new ForgeModDetectorListener(this), this);
        this.loadConfig();
        if (Bukkit.getPluginManager().getPlugin("ProtocolLib") == null) {
            this.getLogger().info("Unable to setup packet listener as ProtocolLib is missing!");
        } else {
            Packets p = new Packets(this);
            ProtocolManager m = ProtocolLibrary.getProtocolManager();
            m.addPacketListener(p);
        }
    }

    public void loadConfig() {
        this.saveDefaultConfig();
        this.reloadConfig();
        this.config.loadBukkitConfig(this.getConfig());
        this.getCoremods().createPayloads();
    }

    public Configs getPluginConfig() {
        return this.config;
    }

    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        if (player.hasMetadata("forge_mods")) {
            List<MetadataValue> metadata = player.getMetadata("forge_mods");
            if (metadata.stream().anyMatch((meta) -> {
                return meta.getOwningPlugin() == this;
            })) {
                return;
            }
        }

        ByteBuf buf = Unpooled.wrappedBuffer(bytes);
        List<String> mods = Utils.getMods(bytes, this.isUsePacketListener() || this.isBungee());
        if (mods != null) {
            this.coremods.onModUser(new BukkitPlayer(this, player), mods);
        }
    }

    public void doMods(Player player) {
        player.removeMetadata("forge_mods", this);
        this.coremods.onModUser(new BukkitPlayer(this, player));
    }

    public boolean isUsePacketListener() {
        return this.usePacketListener;
    }

    public Core getCoremods() {
        return this.coremods;
    }

    public boolean isBungee() {
        return this.bungee;
    }
}
