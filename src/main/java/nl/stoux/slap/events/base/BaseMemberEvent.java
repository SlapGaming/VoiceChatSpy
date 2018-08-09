package nl.stoux.slap.events.base;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;
import nl.stoux.slap.models.Member;
import nl.stoux.slap.models.Server;

@Getter
public abstract class BaseMemberEvent extends BaseEvent {

    private transient Server server;
    private transient Member member;

    @SerializedName("server")
    private String serverId;
    @SerializedName("identifier")
    private String memberId;

    public BaseMemberEvent(String type, Server server, Member member) {
        super("MEMBER_" + type);
        this.server = server;
        this.member = member;

        this.serverId = server.getIdentifier();
        this.memberId = member.getIdentifier();
    }

}
