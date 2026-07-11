package cn.linmoyu.partyapi.channel;

import cn.linmoyu.partyapi.PartyAPI;
import cn.linmoyu.partyapi.event.PartyLoadedEvent;
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

    private void onPluginMessage(String channel, Player player, byte[] data) {
        if (!CHANNEL.equals(channel)) return;
        try {
            DataInputStream dis = new DataInputStream(new ByteArrayInputStream(data));
            String message = dis.readUTF();
            Map<String, String> parsed = parseMessage(message);
            String action = parsed.get("action");
            if (action == null) return;
            plugin.getLogger().info("收到channel消息 <- " + player.getName() + ": " + message);

            plugin.getTaskManager().cancel(player.getUniqueId());

            switch (action) {
                case "getparty":
                    handleGetParty(parsed);
                    break;
                case "kick":
                    handleKick(parsed);
                    break;
                case "invite":
                    handleInvite(parsed);
                    break;
                case "leave":
                    handleLeave(parsed);
                    break;
                case "disband":
                    handleDisband(parsed);
                    break;
                case "getpartyresp":
                    handleGetPartyResp(parsed);
                    break;
                default:
                    break;
            }
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "读取插件消息失败: " + e.getMessage());
        }
    }

    private void handleGetParty(Map<String, String> data) {
        String playerUuidStr = data.get("player");
        if (playerUuidStr == null || playerUuidStr.isEmpty()) return;

        UUID playerUuid;
        try {
            playerUuid = UUID.fromString(playerUuidStr);
        } catch (IllegalArgumentException e) {
            return;
        }

        Player player = Bukkit.getPlayer(playerUuid);
        if (player == null) return;

        PartyInfo party = partyManager.getParty(playerUuid);

        String leaderUuid;
        String membersStr;

        if (party == null) {
            leaderUuid = "";
            membersStr = "";
        } else {
            leaderUuid = party.getLeader().toString();
            List<String> memberUuids = new ArrayList<>();
            for (UUID memberId : party.getMembers()) {
                memberUuids.add(memberId.toString());
            }
            membersStr = String.join(";", memberUuids);
        }

        String response = "action=getpartyresp,player=" + playerUuidStr
                + ",leader=" + leaderUuid
                + ",members=" + membersStr;

        sendPluginMessage(player, response);
        plugin.getLogger().info("getparty请求 <- " + player.getName() + ", 响应 -> " + response);
    }

    private void handleGetPartyResp(Map<String, String> data) {
        String playerUuidStr = data.get("player");
        String leaderUuidStr = data.get("leader");
        String membersStr = data.get("members");

        if (playerUuidStr == null || playerUuidStr.isEmpty()) return;

        UUID playerUuid;
        try {
            playerUuid = UUID.fromString(playerUuidStr);
        } catch (IllegalArgumentException e) {
            return;
        }

        Player player = Bukkit.getPlayer(playerUuid);
        if (player == null) return;

        if (leaderUuidStr == null || leaderUuidStr.isEmpty()) {
            partyManager.removeParty(player.getUniqueId());
            return;
        }

        UUID leaderUuid;
        try {
            leaderUuid = UUID.fromString(leaderUuidStr);
        } catch (IllegalArgumentException e) {
            partyManager.removeParty(player.getUniqueId());
            return;
        }

        Player leader = Bukkit.getPlayer(leaderUuid);
        if (leader == null) return;

        List<UUID> memberIds = new ArrayList<>();
        if (membersStr != null && !membersStr.isEmpty()) {
            String[] uuidParts = membersStr.split(";");
            for (String uuidStr : uuidParts) {
                uuidStr = uuidStr.trim();
                if (uuidStr.isEmpty()) continue;
                try {
                    UUID memberId = UUID.fromString(uuidStr);
                    memberIds.add(memberId);
                } catch (IllegalArgumentException ignored) {
                }
            }
        }

        PartyInfo partyInfo = new PartyInfo(leaderUuid, memberIds);
        partyManager.cacheParty(player.getUniqueId(), partyInfo);
        Bukkit.getPluginManager().callEvent(new PartyLoadedEvent(partyInfo));

        plugin.getTaskManager().cancel(player.getUniqueId());

        plugin.getLogger().info("已缓存队伍信息: 队长=" + leaderUuidStr + ", 成员=" + (membersStr == null ? "无" : membersStr));
    }

    private void handleKick(Map<String, String> data) {
        String playerName = data.get("player");
        String targetName = data.get("target");

        if (playerName == null || targetName == null) return;

        Player player = Bukkit.getPlayerExact(playerName);
        Player target = Bukkit.getPlayerExact(targetName);
        if (player == null || target == null) return;

        boolean success = partyManager.kickMember(player.getUniqueId(), target.getUniqueId());

        String response = "action=kickresp,player=" + playerName
                + ",target=" + targetName
                + ",success=" + success;
        sendPluginMessage(player, response);
        plugin.getLogger().info("kick请求 <- " + playerName + ", 目标=" + targetName + ", 结果=" + success);
    }

    private void handleInvite(Map<String, String> data) {
        String playerName = data.get("player");
        String targetName = data.get("target");

        if (playerName == null || targetName == null) return;

        Player player = Bukkit.getPlayerExact(playerName);
        Player target = Bukkit.getPlayerExact(targetName);
        if (player == null || target == null) return;

        boolean success = partyManager.addMember(player.getUniqueId(), target.getUniqueId());

        String response = "action=inviteresp,player=" + playerName
                + ",target=" + targetName
                + ",success=" + success;
        sendPluginMessage(player, response);
        plugin.getLogger().info("invite请求 <- " + playerName + ", 目标=" + targetName + ", 结果=" + success);
    }

    private void handleLeave(Map<String, String> data) {
        String playerName = data.get("player");
        if (playerName == null) return;

        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) return;

        boolean success = partyManager.leaveParty(player.getUniqueId());

        String response = "action=leaveresp,player=" + playerName
                + ",success=" + success;
        sendPluginMessage(player, response);
        plugin.getLogger().info("leave请求 <- " + playerName + ", 结果=" + success);
    }

    private void handleDisband(Map<String, String> data) {
        String playerName = data.get("player");
        if (playerName == null) return;

        Player player = Bukkit.getPlayerExact(playerName);
        if (player == null) return;

        boolean success = partyManager.disbandParty(player.getUniqueId());

        String response = "action=disbandresp,player=" + playerName
                + ",success=" + success;
        sendPluginMessage(player, response);
        plugin.getLogger().info("disband请求 <- " + playerName + ", 结果=" + success);
    }

    public void requestParty(Player player) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            String msg = "action=getparty,player=" + player.getUniqueId().toString();
            dos.writeUTF(msg);
            player.sendPluginMessage(plugin, CHANNEL, baos.toByteArray());
            plugin.getLogger().info("发送getparty请求 -> " + player.getName());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "发送 getparty 请求失败: " + e.getMessage());
        }
    }

    private void sendPluginMessage(Player player, String message) {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            DataOutputStream dos = new DataOutputStream(baos);
            dos.writeUTF(message);
            player.sendPluginMessage(plugin, CHANNEL, baos.toByteArray());
        } catch (IOException e) {
            plugin.getLogger().log(Level.SEVERE, "发送插件消息失败: " + e.getMessage());
        }
    }

    private Map<String, String> parseMessage(String message) {
        Map<String, String> data = new HashMap<>();
        if (message == null || message.isEmpty()) return data;
        for (String part : message.split(",")) {
            String[] kv = part.split("=", 2);
            if (kv.length == 2) {
                data.put(kv[0].trim(), kv[1].trim());
            }
        }
        return data;
    }
}