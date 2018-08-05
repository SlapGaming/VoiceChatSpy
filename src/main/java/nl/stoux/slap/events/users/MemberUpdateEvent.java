package nl.stoux.slap.events.users;

import lombok.Getter;
import nl.stoux.slap.events.base.BaseMemberEvent;
import nl.stoux.slap.models.Member;
import nl.stoux.slap.models.Server;

@Getter
public class MemberUpdateEvent extends BaseMemberEvent {

    private String name;
    private boolean muted;
    private boolean deafened;
    private boolean microphoneDisabled;

    public MemberUpdateEvent(Server server, Member member) {
        super("UPDATE", server, member);
        this.name = member.getName();
        this.muted = member.isMuted();
        this.deafened = member.isDeafened();
        this.microphoneDisabled = member.isMicrophoneDisabled();
    }
}
