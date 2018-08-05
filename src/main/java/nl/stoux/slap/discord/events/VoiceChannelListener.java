package nl.stoux.slap.discord.events;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.channel.voice.GenericVoiceChannelEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelCreateEvent;
import net.dv8tion.jda.core.events.channel.voice.VoiceChannelDeleteEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateNameEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateParentEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdatePositionEvent;
import net.dv8tion.jda.core.events.channel.voice.update.VoiceChannelUpdateUserLimitEvent;
import net.dv8tion.jda.core.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.core.hooks.EventListener;
import nl.stoux.slap.discord.DiscordController;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class VoiceChannelListener implements EventListener {

    private final Logger logger;
    private final DiscordController discord;

    public VoiceChannelListener(DiscordController discord) {
        this.discord = discord;
        this.logger = LogManager.getLogger(getClass().getName());
    }

    @Override
    public void onEvent(Event event) {
        if (event instanceof GenericGuildVoiceEvent) {
            this.handleGuildVoiceEvent((GenericGuildVoiceEvent) event);
        } else if (event instanceof GenericVoiceChannelEvent) {
            this.handleVoiceChannelEvent((GenericVoiceChannelEvent) event);
        }
    }

    private void handleGuildVoiceEvent(GenericGuildVoiceEvent event) {
        this.discord.rebuild(); // TODO
        // TODO: Determine if move/left/join or mute/mic thing

    }

    private void handleVoiceChannelEvent(GenericVoiceChannelEvent event) {
        if (isMajorChannelChange(event)) {
            this.discord.rebuild();
        } else if (event instanceof VoiceChannelUpdateNameEvent) {
            // Update the name of the channel
            // TODO: For now just rebuild
            this.discord.rebuild();
        } else if (event instanceof VoiceChannelUpdateUserLimitEvent) {
            // Update the user limit of the channel
            // TODO: For now just rebuild
            this.discord.rebuild();
        }
    }

    private boolean isMajorChannelChange(GenericVoiceChannelEvent event) {
        return event instanceof VoiceChannelCreateEvent ||
                event instanceof VoiceChannelDeleteEvent ||
                event instanceof VoiceChannelUpdatePositionEvent ||
                event instanceof VoiceChannelUpdateParentEvent;
    }

}
