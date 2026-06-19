package cn.linmoyu.partyapi.channel;

import cn.linmoyu.partyapi.PartyAPI;
import cn.linmoyu.partyapi.manager.PartyManager;
import cn.linmoyu.partyapi.model.PartyInfo;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class ChannelHandler {

    public static final String CHANNEL = "aerparty:main";
    private final PartyAPI plugin;
    private final PartyManager partyManager;

    public ChannelHandler(PartyAPI plugin, PartyManager partyManager) {
        this.plugin = plugin;
        this.partyManager = partyManager;
    }

    public void register() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(plugin, CHANNEL);
        Bukkit.getMessenger().registerIncomingPluginChannel(plugin, CHANNEL, this::onPluginMessage);
    }

    public void unregister() {
        Bukkit.getMessenger().unregisterOutgoingPluginChannel(plugin, CHANNEL);
        Bukkit.getMessenger().unregisterIncomingPluginChannel(plugin, CHANNEL);
    }

    public void requestParty(Player player) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF("action=getparty,player=" + player.getName());
            player.sendPluginMessage(plugin, CHANNEL, baos.toByteArray());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "发送 getparty 请求失败: " + e.getMessage());
        }
    }

    private void onPluginMessage(String channel, Player player, byte[] data) {
        if (!CHANNEL.equals(channel)) {
            return;
        }
        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
            String message = dis.readUTF();
            Map<String, String> parsed = parseMessage(message);
            String action = parsed.get("action");
            if (action == null) return;

            if ("getpartyresp".equals(action)) {
                handleGetPartyResp(parsed);
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "读取插件消息失败: " + e.getMessage());
        }
    }

    private void handleGetPartyResp(Map<String, String> data) {
        String playerName = data.get("player");
        String leaderName = data.get("leader");
        String membersStr = data.get("members");

        if (playerName == null || playerName.isEmpty()) return;

        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) return;

        if (leaderName == null || leaderName.isEmpty()) {
            partyManager.removeParty(player.getUniqueId());
            return;
        }

        Player leader = Bukkit.getPlayerExact(leaderName);
        if (leader == null) return;

        List<UUID> memberIds = new ArrayList<>();
        if (membersStr != null && !membersStr.isEmpty()) {
            String[] memberNames = membersStr.split(";");
            for (String name : memberNames) {
                name = name.trim();
                if (name.isEmpty()) continue;
                Player member = Bukkit.getPlayerExact(name);
                if (member != null) {
                    memberIds.add(member.getUniqueId());
                }
            }
        }

        PartyInfo partyInfo = new PartyInfo(leader.getUniqueId(), memberIds);
        partyManager.cacheParty(player.getUniqueId(), partyInfo);

        plugin.getLogger().info("已缓存队伍信息: 队长=" + leaderName + ", 成员=" + (membersStr == null ? "无" : membersStr));
    }

    private Map<String, String> parseMessage(String message) {
        Map<String, String> data = new HashMap<>();
        for (String part : message.split(",")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) {
                data.put(kv[0].trim(), kv[1].trim());
            }
        }
        return data;
    }
}
