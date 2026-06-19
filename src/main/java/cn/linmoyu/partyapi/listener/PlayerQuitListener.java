package cn.linmoyu.partyapi.listener;

import cn.linmoyu.partyapi.PartyAPI;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerQuitListener implements Listener {

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        PartyAPI.getPlugin().getPartyManager().removeParty(event.getPlayer().getUniqueId());
    }
}
