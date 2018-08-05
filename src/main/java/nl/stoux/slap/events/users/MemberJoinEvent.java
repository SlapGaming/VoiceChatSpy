package nl.stoux.slap.events.users;

import lombok.Getter;
import nl.stoux.slap.events.base.BaseMemberEvent;
import nl.stoux.slap.models.Channel;
import nl.stoux.slap.models.Member;
import nl.stoux.slap.models.Server;

@Getter
public class MemberJoinEvent extends BaseMemberEvent {

    private String channelId;
    private Member member;

    public MemberJoinEvent(Server server, Member member, Channel joinedChannel) {
        super("JOIN", server, member);
        this.member = member;
        this.channelId = joinedChannel.getIdentifier();
    }

}
