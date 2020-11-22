package nl.stoux.slap.discord.events;

import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.api.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.api.events.channel.voice.update.*;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.stoux.slap.App;
import nl.stoux.slap.discord.DiscordController;
import nl.stoux.slap.discord.models.DiscordGuild;
import nl.stoux.slap.discord.models.DiscordVoiceChannel;
import nl.stoux.slap.events.channels.ChannelUpdateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VoiceChannelListener extends ListenerAdapter {

    private final Logger logger;
    private final DiscordController discord;

    public VoiceChannelListener(DiscordController discord) {
        this.discord = discord;
        this.logger = LogManager.getLogger(getClass().getName());
    }

    // Minor events

    @Override
    public void onVoiceChannelUpdateName(VoiceChannelUpdateNameEvent event) {
        updateChannel(event);
    }

    @Override
    public void onVoiceChannelUpdateUserLimit(VoiceChannelUpdateUserLimitEvent event) {
        updateChannel(event);
    }

    private void updateChannel(GenericVoiceChannelUpdateEvent updateEvent) {
        DiscordGuild guild = discord.getGuild(updateEvent.getGuild().getIdLong());
        VoiceChannel channel = updateEvent.getChannel();
        DiscordVoiceChannel voiceChannel = guild.getVoiceChannel(channel.getIdLong());

        voiceChannel.setName(voiceChannel.getName());
        voiceChannel.setUserLimit((long) channel.getUserLimit());

        logger.info("Updating channel {} ({}) in guild {}",
                voiceChannel.getName(), voiceChannel.getId(), guild.getName());

        App.post(new ChannelUpdateEvent(guild, voiceChannel));
    }

    // Major events

    @Override
    public void onVoiceChannelDelete(VoiceChannelDeleteEvent event) {
        logger.info("Channel deleted! Rebuilding..");
        this.discord.rebuildGuild(event.getGuild().getIdLong());
    }

    @Override
    public void onVoiceChannelUpdatePosition(VoiceChannelUpdatePositionEvent event) {
        logger.info("Channel position updated! Rebuilding..");
        this.discord.rebuildGuild(event.getGuild().getIdLong());
    }

    @Override
    public void onVoiceChannelUpdateParent(VoiceChannelUpdateParentEvent event) {
        logger.info("Channel's parent updated! Rebuilding..");
        this.discord.rebuildGuild(event.getGuild().getIdLong());
    }

    @Override
    public void onVoiceChannelCreate(VoiceChannelCreateEvent event) {
        logger.info("Channel created! Rebuilding..");
        this.discord.rebuildGuild(event.getGuild().getIdLong());
    }
}
