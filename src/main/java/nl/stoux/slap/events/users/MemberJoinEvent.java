package nl.stoux.slap.events.users;

import lombok.Getter;
import nl.stoux.slap.events.base.BaseIdentifiableEvent;
import nl.stoux.slap.models.Member;
import nl.stoux.slap.models.Server;

@Getter
public class MemberJoinEvent extends BaseIdentifiableEvent {

    private Member member;

    public MemberJoinEvent(Server server, Member member) {
        super("MEMBER_JOIN", server, member);
        this.member = member;
    }

}
