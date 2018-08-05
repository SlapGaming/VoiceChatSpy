package nl.stoux.slap.discord.models;

import lombok.Getter;
import lombok.Setter;
import nl.stoux.slap.discord.models.helpers.DiscordId;
import nl.stoux.slap.models.Member;

public class DiscordVoiceMember extends DiscordId implements Member {

    @Setter
    @Getter
    private boolean muted;

    @Setter
    @Getter
    private boolean deafened;

    public DiscordVoiceMember(Long id, String name) {
        super(id, name);
        muted = false;
        deafened = false;
    }

    @Override
    public String getIdentifier() {
        return "DI-MEMBER:" + this.getId();
    }

    @Override
    public boolean isMicrophoneDisabled() {
        return false;
    }

}
