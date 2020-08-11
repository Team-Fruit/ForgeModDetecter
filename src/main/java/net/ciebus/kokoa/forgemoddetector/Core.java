package net.ciebus.kokoa.forgemoddetector;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class Core {
    private byte[] fmlHandshake;
    private byte[] fmlRegistries;
    private final Cache<String, List<String>> forgeMods;
    private final Configs config;

    public Core(Configs config) {
        this.forgeMods = CacheBuilder.newBuilder().expireAfterWrite(3L, TimeUnit.MINUTES).build();
        this.config = config;
    }

    public void createPayloads() {
        ArrayList<String> channels = new ArrayList<>();
        /*
        Iterator var2 = this.config.getEntities().iterator();

        while(var2.hasNext()) {
            CustomEntity entity = (CustomEntity)var2.next();
            if (entity.getChannels() != null) {
                channels.addAll(entity.getChannels());
            }
        }*/

        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream output = new DataOutputStream(stream);

        //ModList packet
        Iterator<String> var4;
        try {
            Utils.writeInt(output, 1);
            Utils.writeInt(output, 0);
            Utils.writeInt(output, channels.size());
            var4 = channels.iterator();

            while(var4.hasNext()) {
                String channel = var4.next();
                Utils.writeString(output, channel.substring(0, channel.indexOf("|")));
                Utils.writeString(output, channel.substring(channel.indexOf("|") + 1));
            }

            Utils.writeInt(output, 0);
        } catch (IOException var7) {
            var7.printStackTrace();
        }
        this.fmlHandshake = stream.toByteArray();
    }

    public void onModUser(LibPlayer player) {
        List<String> mods = (List<String>)this.getForgeMods().getIfPresent(player.getName());
        if (mods != null) {
            this.getForgeMods().invalidate(player.getName());
            this.onModUser(player, mods);
        }
    }

    public void onModUser(LibPlayer player, List<String> mods) {
        if (mods.size() == 1 && ((String)mods.get(0)).equals("fabric")) {
            player.doLogger(player.getName() + " is attempting to join with a Fabric Client..");
        } else {
            player.doLogger(player.getName() + " is attempting to join with the mods: " + Arrays.toString(mods.toArray(new String[0])));
        }

        for (Player aplayer : Bukkit.getOnlinePlayers()) {
            if(aplayer.hasPermission("forgemoddetector.admin")) {
                aplayer.sendMessage(player.getName() + " installed forge mods: " + Arrays.toString(mods.toArray(new String[0])));
            } else {
                aplayer.sendMessage(player.getName() + " join the game");
            }
        }
        player.saveMods(mods);
    }

    public byte[] getFmlHandshake() {
        return this.fmlHandshake;
    }

    public byte[] getFmlRegistries() {
        return this.fmlRegistries;
    }

    public Cache<String, List<String>> getForgeMods() {
        return this.forgeMods;
    }
}

