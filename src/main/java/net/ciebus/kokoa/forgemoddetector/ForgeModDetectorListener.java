package net.ciebus.kokoa.forgemoddetector;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRegisterChannelEvent;
import org.bukkit.metadata.MetadataValue;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgeModDetectorListener implements Listener {
    private ForgeModDetector plugin;

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        // event.setJoinMessage(null);
        this.plugin.doMods(event.getPlayer());
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        event.getPlayer().removeMetadata("forge_mods",plugin);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent event) {
        // List<MetadataValue> v = event.getPlayer().getMetadata("forge_mods");
        //TODO
        Pattern pattern = Pattern.compile("<:.+:.+>");
        Matcher matcher = pattern.matcher(event.getMessage());
        boolean matchResult = matcher.matches();
        if(matchResult) {
            event.setCancelled(true);
            System.out.println("<" + event.getPlayer().getName() + "> " + event.getMessage());
            for (Player aplayer : Bukkit.getOnlinePlayers()) {
                List<MetadataValue> v = aplayer.getMetadata("forge_mods");
                String[] tmp = event.getMessage().split(":");
                if(v.isEmpty() || v.get(0) == null) {
                    aplayer.sendMessage("<" + event.getPlayer().getName() + ">" + ChatColor.GREEN +  plugin.getPluginConfig().getGlobalMessage() + "(:" + tmp[1] + ":)");
                } else if(!((List<String>)(v.get(0).value())).contains("emojicord")) {
                    aplayer.sendMessage("<" + event.getPlayer().getName() + ">" + ChatColor.GREEN +  plugin.getPluginConfig().getGlobalMessage() + "(:" + tmp[1] + ":)");
                } else {
                    aplayer.sendMessage("<" + event.getPlayer().getName() + ">" + event.getMessage());
                }
            }
        }
    }

    public ForgeModDetectorListener(ForgeModDetector plugin) {
        this.plugin = plugin;
    }
}
