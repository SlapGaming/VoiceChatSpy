package nl.stoux.slap.teamspeak.models;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import nl.stoux.slap.models.Member;

@Setter
@Getter
public class TeamspeakUser implements Member {

    @Setter(AccessLevel.NONE)
    private int id;
    private String nickname;
    private String group;
    private boolean muted;
    private boolean deafened;
    private boolean microphoneDisabled;
    @Setter(AccessLevel.NONE)
    private TeamspeakChannel channel;

    private int[] serverGroups;

    public TeamspeakUser(int id, String nickname, boolean muted, boolean deafened, boolean microphoneDisabled, String groupPrefix, int[] serverGroups) {
        this.id = id;
        this.nickname = nickname;
        this.group = groupPrefix;
        this.muted = muted;
        this.deafened = deafened;
        this.microphoneDisabled = microphoneDisabled;
        this.serverGroups = serverGroups;
    }

    @Override
    public String getName() {
        return group == null ? nickname : group + " " + nickname;
    }

    @Override
    public String getIdentifier() {
        return "TS-USER:" + id;
    }

    public void moveToChannel(TeamspeakChannel channel) {
        if (this.channel != null) {
            this.channel.removeUser(this);
        }
        this.channel = channel;
        if (this.channel != null) {
            this.channel.addUser(this);
        }
    }

}
