package nl.stoux.slap.events.users;

import nl.stoux.slap.events.base.BaseIdentifiableEvent;
import nl.stoux.slap.models.Member;
import nl.stoux.slap.models.Server;

public class MemberLeaveEvent extends BaseIdentifiableEvent {

    public MemberLeaveEvent(Server server, Member member) {
        super("MEMBER_LEAVE", server, member);
    }

}
