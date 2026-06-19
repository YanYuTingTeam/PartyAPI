package cn.linmoyu.partyapi.manager;

import cn.linmoyu.partyapi.model.PartyInfo;

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

    public void clearAll() {
        partyCache.clear();
    }
}
