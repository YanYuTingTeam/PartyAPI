package cn.linmoyu.partyapi.event;

import cn.linmoyu.partyapi.model.PartyInfo;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyCreateEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final PartyInfo partyInfo;
    @Getter
    private final java.util.List<java.util.UUID> memberIds;

    public PartyCreateEvent(PartyInfo partyInfo, java.util.List<java.util.UUID> memberIds) {
        this.partyInfo = partyInfo;
        this.memberIds = memberIds;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
