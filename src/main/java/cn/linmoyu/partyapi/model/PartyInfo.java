package cn.linmoyu.partyapi.model;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class PartyInfo {

    @Getter
    private final UUID leader;
    private final List<UUID> members;

    public PartyInfo(UUID leader, List<UUID> members) {
        this.leader = leader;
        this.members = members;
    }

    public List<UUID> getMembers() {
        return new ArrayList<>(members);
    }

    public List<UUID> getAllMembers() {
        List<UUID> all = new ArrayList<>();
        all.add(leader);
        all.addAll(members);
        return all;
    }

    public int getSize() {
        return members.size() + 1;
    }

    public boolean isLeader(UUID playerId) {
        return leader.equals(playerId);
    }

    public boolean isMember(UUID playerId) {
        return leader.equals(playerId) || members.contains(playerId);
    }
}
