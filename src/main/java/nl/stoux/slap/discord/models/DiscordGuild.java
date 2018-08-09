package nl.stoux.slap.discord.models;

import nl.stoux.slap.discord.models.helpers.DiscordId;
import nl.stoux.slap.discord.models.helpers.DiscordListContainer;
import nl.stoux.slap.models.Channel;
import nl.stoux.slap.models.Server;

import java.util.List;

public class DiscordGuild extends DiscordId implements Server {

    private final DiscordListContainer<DiscordVoiceChannel> voiceChannels;

    public DiscordGuild(Long id, String name) {
        super(id, name);
        this.voiceChannels = new DiscordListContainer<>();
    }

    @Override
    public String getIdentifier() {
        return "DI-GUILD:" + this.getId();
    }

    @Override
    public String getType() {
        return "DC";
    }

    @Override
    public List<? extends Channel> getChannels() {
        return voiceChannels.getList();
    }

    @Override
    public Long getUsersOnline() {
        int users = 0;
        for (DiscordVoiceChannel voiceChannel : voiceChannels.getList()) {
            users += voiceChannel.getMembers().size();
        }
        return (long) users;
    }

    @Override
    public Long getMaxUsers() {
        return null;
    }

    public void addVoiceChannel(DiscordVoiceChannel channel) {
        this.voiceChannels.add(channel);
    }

    public DiscordVoiceChannel getVoiceChannel(long id) {
        return voiceChannels.getItem(id);
    }

    public DiscordVoiceMember getMember(long voiceChannelId, long memberId) {
        DiscordVoiceChannel voiceChannel = getVoiceChannel(voiceChannelId);
        return voiceChannel != null ? voiceChannel.getMember(memberId) : null;
    }

}
