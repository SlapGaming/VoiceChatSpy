package nl.stoux.slap.events.base;

import lombok.Getter;
import nl.stoux.slap.models.Channel;
import nl.stoux.slap.models.Member;
import nl.stoux.slap.models.Server;

@Getter
public abstract class BaseChannelEvent extends BaseEvent {

    private String server;
    private String identifier;

    public BaseChannelEvent(String type, Server server, Channel channel) {
        super("CHANNEL_" + type);
        this.server = server.getIdentifier();
        this.identifier = channel.getIdentifier();
    }

}
