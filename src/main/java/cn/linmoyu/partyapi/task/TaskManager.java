package cn.linmoyu.partyapi.task;

import org.bukkit.scheduler.BukkitTask;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class TaskManager {
    private final Map<UUID, BukkitTask> activeTasks = new HashMap<>();

    public void register(UUID uuid, BukkitTask task) {
        cancel(uuid);
        activeTasks.put(uuid, task);
    }

    public void cancel(UUID uuid) {
        BukkitTask task = activeTasks.remove(uuid);
        if (task != null) {
            task.cancel();
        }
    }

    public boolean hasTask(UUID uuid) {
        return activeTasks.containsKey(uuid);
    }
}
