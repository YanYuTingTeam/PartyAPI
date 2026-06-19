package cn.linmoyu.partyapi;

import cn.linmoyu.partyapi.channel.ChannelHandler;
import cn.linmoyu.partyapi.listener.PlayerJoinListener;
import cn.linmoyu.partyapi.listener.PlayerQuitListener;
import cn.linmoyu.partyapi.manager.PartyManager;
import lombok.Getter;
import org.bukkit.plugin.java.JavaPlugin;

public final class PartyAPI extends JavaPlugin {

    @Getter
    private static PartyAPI plugin;

    @Getter
    private PartyManager partyManager;
    @Getter
    private ChannelHandler channelHandler;

    @Override
    public void onEnable() {
        plugin = this;

        partyManager = new PartyManager();
        channelHandler = new ChannelHandler(this, partyManager);
        channelHandler.register();

        getServer().getPluginManager().registerEvents(new PlayerJoinListener(), this);
        getServer().getPluginManager().registerEvents(new PlayerQuitListener(), this);

        getLogger().info("PartyAPI 已启用.");
    }

    @Override
    public void onDisable() {
        if (channelHandler != null) {
            channelHandler.unregister();
        }
        if (partyManager != null) {
            partyManager.clearAll();
        }
        getLogger().info("PartyAPI 已禁用.");
    }
}
