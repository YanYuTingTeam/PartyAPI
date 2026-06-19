package cn.linmoyu.partyapi.manager;

import cn.linmoyu.partyapi.model.PartyInfo;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class PartyManager {

    private final Map<UUID, PartyInfo> partyCache = new ConcurrentHashMap<>();

    public void cacheParty(UUID playerId, PartyInfo partyInfo) {
        if (partyInfo == null) {
            partyCache.remove(playerId);
            return;
        }
        for (UUID member : partyInfo.getAllMembers()) {
            partyCache.put(member, partyInfo);
        }
    }

    public PartyInfo getParty(UUID playerId) {
        return partyCache.get(playerId);
    }

    public boolean hasParty(UUID playerId) {
        return partyCache.containsKey(playerId);
    }

    public boolean isLeader(UUID playerId) {
        PartyInfo party = partyCache.get(playerId);
        return party != null && party.isLeader(playerId);
    }

    public void removeParty(UUID playerId) {
        PartyInfo party = partyCache.remove(playerId);
        if (party != null) {
            for (UUID member : party.getAllMembers()) {
                partyCache.remove(member);
            }
        }
    }

    public void createParty(UUID leaderId, List<UUID> memberIds) {
        PartyInfo partyInfo = new PartyInfo(leaderId, memberIds);
        cacheParty(leaderId, partyInfo);
    }

    public boolean kickMember(UUID operatorId, UUID targetId) {
        PartyInfo party = partyCache.get(operatorId);
        if (party == null || !party.isLeader(operatorId)) return false;
        if (!party.isMember(targetId)) return false;
        party.removeMember(targetId);
        partyCache.remove(targetId);
        if (party.getSize() <= 1) {
            disbandParty(operatorId);
        }
        return true;
    }

    public boolean addMember(UUID leaderId, UUID memberId) {
        PartyInfo party = partyCache.get(leaderId);
        if (party == null || !party.isLeader(leaderId)) return false;
        if (party.isFull()) return false;
        party.addMember(memberId);
        partyCache.put(memberId, party);
        return true;
    }

    public boolean leaveParty(UUID playerId) {
        PartyInfo party = partyCache.get(playerId);
        if (party == null) return false;
        if (party.isLeader(playerId)) {
            disbandParty(playerId);
            return true;
        }
        party.removeMember(playerId);
        partyCache.remove(playerId);
        return true;
    }

    public boolean disbandParty(UUID operatorId) {
        PartyInfo party = partyCache.remove(operatorId);
        if (party == null) return false;
        for (UUID member : party.getAllMembers()) {
            partyCache.remove(member);
        }
        return true;
    }

    public void clearAll() {
        partyCache.clear();
    }
}
