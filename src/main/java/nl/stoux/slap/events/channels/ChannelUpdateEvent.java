package nl.stoux.slap.events.channels;

import nl.stoux.slap.events.base.BaseChannelEvent;
import nl.stoux.slap.models.Channel;
import nl.stoux.slap.models.Server;

public class ChannelUpdateEvent extends BaseChannelEvent {

    private String name;
    private Long userLimit;

    public ChannelUpdateEvent(Server server, Channel channel) {
        super("UPDATE", server, channel);
        this.name = channel.getName();
        this.userLimit = channel.getUserLimit();
    }
}
