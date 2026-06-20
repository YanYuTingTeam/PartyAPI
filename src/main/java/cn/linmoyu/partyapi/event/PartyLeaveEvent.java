package cn.linmoyu.partyapi.event;

import cn.linmoyu.partyapi.model.PartyInfo;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PartyLeaveEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final PartyInfo partyInfo;
    @Getter
    private final UUID playerId;

    public PartyLeaveEvent(PartyInfo partyInfo, UUID playerId) {
        this.partyInfo = partyInfo;
        this.playerId = playerId;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
