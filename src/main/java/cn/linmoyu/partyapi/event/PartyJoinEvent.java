package cn.linmoyu.partyapi.event;

import cn.linmoyu.partyapi.model.PartyInfo;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PartyJoinEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final PartyInfo partyInfo;
    @Getter
    private final UUID playerId;
    @Getter
    private final UUID invitedBy;

    public PartyJoinEvent(PartyInfo partyInfo, UUID playerId, UUID invitedBy) {
        this.partyInfo = partyInfo;
        this.playerId = playerId;
        this.invitedBy = invitedBy;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
