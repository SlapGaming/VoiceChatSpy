package nl.stoux.slap.events;

import lombok.Getter;
import nl.stoux.slap.events.base.BaseEvent;
import nl.stoux.slap.models.Server;

@Getter
public class ServerUpdateEvent extends BaseEvent {

    private Server server;

    public ServerUpdateEvent(Server server) {
        super("SERVER_UPDATE");
        this.server = server;
    }

}
