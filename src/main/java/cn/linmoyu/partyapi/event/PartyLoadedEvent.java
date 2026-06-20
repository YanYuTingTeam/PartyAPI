package cn.linmoyu.partyapi.event;

import cn.linmoyu.partyapi.model.PartyInfo;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class PartyLoadedEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final PartyInfo partyInfo;

    public PartyLoadedEvent(PartyInfo partyInfo) {
        this.partyInfo = partyInfo;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
