package nl.stoux.slap.events.users;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import nl.stoux.slap.events.base.BaseMemberEvent;
import nl.stoux.slap.models.Channel;
import nl.stoux.slap.models.Member;
import nl.stoux.slap.models.Server;

@Getter
public class MemberMoveEvent extends BaseMemberEvent {

    private transient Channel fromChannel;
    private transient Channel toChannel;

    @SerializedName("fromChannel")
    private String fromChannelId;
    @SerializedName("toChannel")
    private String toChannelId;

    public MemberMoveEvent(Server server, Member member, Channel fromChannel, Channel toChannel) {
        super("MOVE", server, member);
        this.fromChannel = fromChannel;
        this.toChannel = toChannel;

        this.fromChannelId = fromChannel.getIdentifier();
        this.toChannelId = toChannel.getIdentifier();
    }

}
