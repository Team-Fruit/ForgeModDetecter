package net.ciebus.kokoa.forgemoddetector;

import org.bukkit.entity.Player;
import org.bukkit.metadata.FixedMetadataValue;

import java.util.List;

public class BukkitPlayer implements LibPlayer {
    private ForgeModDetector bukkit;
    private Player player;

    public boolean hasPermission(String permission) {
        return this.player.hasPermission(permission);
    }

    public void doLogger(String message) {
        this.bukkit.getLogger().info(message);
    }

    public void kickPlayer(String message) {
        this.player.kickPlayer(message);
    }

    public String getName() {
        return this.player.getName();
    }

    public void saveMods(List<String> mods) {
        this.player.setMetadata("forge_mods", new FixedMetadataValue(this.bukkit, mods));
    }

    public BukkitPlayer(ForgeModDetector bukkit, Player player) {
        this.bukkit = bukkit;
        this.player = player;
    }
}

interface LibPlayer {
    boolean hasPermission(String var1);

    void doLogger(String var1);

    void kickPlayer(String var1);

    String getName();

    void saveMods(List<String> var1);
}
