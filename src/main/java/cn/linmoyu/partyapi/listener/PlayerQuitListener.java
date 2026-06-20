package cn.linmoyu.partyapi.listener;

import cn.linmoyu.partyapi.PartyAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        UUID playerUUID = event.getPlayer().getUniqueId();
        PartyAPI.getPlugin().getPartyManager().removeParty(playerUUID);
        PartyAPI.getPlugin().getTaskManager().cancel(playerUUID);
    }
}
