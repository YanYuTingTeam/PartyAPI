package cn.linmoyu.partyapi.event;

import cn.linmoyu.partyapi.model.PartyInfo;
import lombok.Getter;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import java.util.UUID;

public class PartyKickEvent extends Event {

    private static final HandlerList handlers = new HandlerList();
    @Getter
    private final PartyInfo partyInfo;
    @Getter
    private final UUID operatorId;
    @Getter
    private final UUID targetId;

    public PartyKickEvent(PartyInfo partyInfo, UUID operatorId, UUID targetId) {
        this.partyInfo = partyInfo;
        this.operatorId = operatorId;
        this.targetId = targetId;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

    public HandlerList getHandlers() {
        return handlers;
    }
}
