package nl.stoux.slap.events.base;

import lombok.Getter;
import nl.stoux.slap.models.Member;
import nl.stoux.slap.models.Server;

@Getter
public abstract class BaseIdentifiableEvent extends BaseEvent {

    private String server;
    private String identifier;

    public BaseIdentifiableEvent(String type, Server server, Member member) {
        super(type);
        this.server = server.getIdentifier();
        this.identifier = member.getIdentifier();
    }

}
