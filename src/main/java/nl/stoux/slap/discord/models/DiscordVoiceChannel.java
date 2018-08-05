package nl.stoux.slap.discord.models;

import lombok.Getter;
import lombok.Setter;
import nl.stoux.slap.discord.models.helpers.DiscordId;
import nl.stoux.slap.discord.models.helpers.DiscordListContainer;
import nl.stoux.slap.models.Channel;
import nl.stoux.slap.models.Member;

import java.util.List;

public class DiscordVoiceChannel extends DiscordId implements Channel {

    private final DiscordListContainer<DiscordVoiceMember> members;

    @Setter
    @Getter
    private Long userLimit;

    public DiscordVoiceChannel(Long id, String name, Long userLimit) {
        super(id, name);
        this.userLimit = userLimit;
        this.members = new DiscordListContainer<>();
    }

    @Override
    public String getIdentifier() {
        return "DI-CHANNEL:" + this.getId();
    }

    @Override
    public List<? extends Member> getMembers() {
        return members.getList();
    }

    @Override
    public List<? extends Channel> getChildChannels() {
        return null;
    }

    public void addMember(DiscordVoiceMember member) {
        this.members.add(member);
    }

    public void removeMember(DiscordVoiceMember member) {
        this.members.remove(member);
    }

    public DiscordVoiceMember getMember(long id) {
        return members.getItem(id);
    }
}
