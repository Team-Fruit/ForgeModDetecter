package net.ciebus.kokoa.forgemoddetector;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;

import java.util.List;
import java.util.concurrent.TimeUnit;

public abstract class CorePackets {
    private final Cache<String, String> loginAttempts;
    private final int handshakeInitId;
    private final int handshakeRegistryId;
    private final Core mods;

    public CorePackets(Core mods) {
        this.loginAttempts = CacheBuilder.newBuilder().expireAfterWrite(15L, TimeUnit.SECONDS).build();
        this.handshakeInitId = 5555551;
        this.handshakeRegistryId = 5555552;
        this.mods = mods;
    }

    private void handleModlist(String name, byte[] data) {
        List<String> mods = Utils.getMods(data, true);
        if (mods != null) {
            this.getMods().getForgeMods().put(name, mods);
        }
    }

    public void onLoginPayload(Object player, String address, int id, byte[] bytes) {
        String name = (String)this.loginAttempts.getIfPresent(address);
        if (name != null) {
            if (id == 5555551) {
                this.handleHandshake(player, address, bytes);
            } else if (id == 5555552) {
                this.handleRegistryAccepted(player, address);
            }

        }
    }

    private void handleRegistryAccepted(Object player, String address) {
        String name = (String)this.loginAttempts.getIfPresent(address);
        if (name != null) {
            this.loginAttempts.invalidate(address);
            this.sendFinishLogin(player, name);
        }
    }

    private void handleHandshake(Object player, String address, byte[] bytes) {
        String name = (String)this.loginAttempts.getIfPresent(address);
        if (bytes != null) {
            this.handleModlist(name, bytes);
            if (this.getMods().getFmlRegistries() == null) {
                this.handleRegistryAccepted(player, address);
            } else {
                this.sendRegistries(player);
            }
        } else {
            this.handleRegistryAccepted(player, address);
        }

    }

    public final void onLoginStart(Object player, String address, String name) {
        this.getLoginAttempts().put(address, name);
        this.sendHandshakeStart(player);
    }

    public abstract void sendHandshakeStart(Object var1);

    public abstract void sendRegistries(Object var1);

    public abstract void sendFinishLogin(Object var1, String var2);

    public Cache<String, String> getLoginAttempts() {
        return this.loginAttempts;
    }

    public int getHandshakeInitId() {
        this.getClass();
        return 5555551;
    }

    public int getHandshakeRegistryId() {
        this.getClass();
        return 5555552;
    }

    public Core getMods() {
        return this.mods;
    }
}
