package cn.linmoyu.partyapi.listener;

import cn.linmoyu.partyapi.PartyAPI;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class PlayerJoinListener implements Listener {
    private static final long QUE_TIMEOUT = 15000;
    private static final long RETRY_INTERVAL = 5L;
    private final Map<UUID, Long> pendingQue = new HashMap<>();
    private final Map<UUID, BukkitTask> activeTasks = new HashMap<>();

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        UUID uuid = player.getUniqueId();
        long startTime = System.currentTimeMillis();
        pendingQue.put(uuid, startTime);
        BukkitTask task = new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - startTime > QUE_TIMEOUT) {
                    cancelTask(uuid);
                    return;
                }
                if (!player.isOnline() || !pendingQue.containsKey(uuid)) {
                    cancelTask(uuid);
                    return;
                }
                PartyAPI.getPlugin().getChannelHandler().requestParty(player);
            }
        }.runTaskTimer(PartyAPI.getPlugin(), RETRY_INTERVAL, RETRY_INTERVAL);
        activeTasks.put(uuid, task);
    }

    public void cancelTask(UUID uuid) {
        pendingQue.remove(uuid);
        BukkitTask task = activeTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }
}
