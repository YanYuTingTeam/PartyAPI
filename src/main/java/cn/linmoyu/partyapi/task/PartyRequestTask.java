package cn.linmoyu.partyapi.task;

import cn.linmoyu.partyapi.PartyAPI;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import java.util.UUID;

public class PartyRequestTask extends BukkitRunnable {
    private static final long QUE_TIMEOUT = 15000;
    private static final long RETRY_INTERVAL = 5L;

    private final Player player;
    private final UUID uuid;
    private final long startTime;

    public PartyRequestTask(Player player, UUID uuid) {
        this.player = player;
        this.uuid = uuid;
        this.startTime = System.currentTimeMillis();
    }

    public void start() {
        BukkitTask task = runTaskTimer(PartyAPI.getPlugin(), RETRY_INTERVAL, RETRY_INTERVAL);
        PartyAPI.getPlugin().getTaskManager().register(uuid, task);
    }

    @Override
    public void run() {
        TaskManager taskManager = PartyAPI.getPlugin().getTaskManager();
        if (System.currentTimeMillis() - startTime > QUE_TIMEOUT || !player.isOnline() || !taskManager.hasTask(uuid)) {
            taskManager.cancel(uuid);
            return;
        }
        PartyAPI.getPlugin().getChannelHandler().requestParty(player);
    }
}
