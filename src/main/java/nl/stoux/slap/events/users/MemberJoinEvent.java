package nl.stoux.slap.events.users;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import nl.stoux.slap.events.base.BaseMemberEvent;
import nl.stoux.slap.models.Channel;
import nl.stoux.slap.models.Member;
import nl.stoux.slap.models.Server;

@Getter
public class MemberJoinEvent extends BaseMemberEvent {

    private transient Channel joinedChannel;
    private Member member;

    @SerializedName("joinedChannel")
    private String joinedChannelId;

    public MemberJoinEvent(Server server, Member member, Channel joinedChannel) {
        super("JOIN", server, member);
        this.member = member;
        this.joinedChannel = joinedChannel;

        this.joinedChannelId = joinedChannel.getIdentifier();
    }

}
