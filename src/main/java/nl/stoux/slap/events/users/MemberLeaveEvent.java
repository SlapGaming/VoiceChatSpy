package nl.stoux.slap.events.users;

import nl.stoux.slap.events.base.BaseMemberEvent;
import nl.stoux.slap.models.Member;
import nl.stoux.slap.models.Server;

public class MemberLeaveEvent extends BaseMemberEvent {

    public MemberLeaveEvent(Server server, Member member) {
        super("LEAVE", server, member);
    }

}
