package nl.stoux.slap.events.users;

import lombok.Getter;
import nl.stoux.slap.events.base.BaseIdentifiableEvent;
import nl.stoux.slap.models.Channel;
import nl.stoux.slap.models.Member;
import nl.stoux.slap.models.Server;

@Getter
public class MemberMoveEvent extends BaseIdentifiableEvent {

    private String fromChannel;
    private String toChannel;

    public MemberMoveEvent(Server server, Member member, Channel fromChannel, Channel toChannel) {
        super("MEMBER_MOVE", server, member);
        this.fromChannel = fromChannel.getIdentifier();
        this.toChannel = toChannel.getIdentifier();
    }
}
