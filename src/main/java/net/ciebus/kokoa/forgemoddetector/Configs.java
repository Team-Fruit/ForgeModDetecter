package net.ciebus.kokoa.forgemoddetector;
import org.bukkit.configuration.file.FileConfiguration;


public class Configs {
    private String globalMessage;

    public Configs() {
    }

    public void loadBukkitConfig(FileConfiguration config) {
        this.globalMessage = config.getString("Message");
    }
    public String getGlobalMessage() {
        return this.globalMessage;
    }
}

