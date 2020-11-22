package nl.stoux.slap.discord.events;

import com.google.common.eventbus.Subscribe;
import net.dv8tion.jda.api.JDA;
import net.dv8tion.jda.api.entities.TextChannel;
import nl.stoux.slap.App;
import nl.stoux.slap.config.Config;
import nl.stoux.slap.events.users.MemberJoinEvent;
import nl.stoux.slap.events.users.MemberLeaveEvent;
import nl.stoux.slap.events.users.MemberMoveEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.Optional;

public class NsaListener {

    private final Logger logger = LogManager.getLogger(getClass().getSimpleName());

    private TextChannel textChannel;

    public NsaListener(JDA jda, Config config) {
        Optional<String> optChannelId = config.getDiscordNsaChannel();

        if (!optChannelId.isPresent()) {
            logger.info("No NSA channel/guild IDs given.");
            return;
        }

        textChannel = jda.getTextChannelById(optChannelId.get());
        if (textChannel == null) {
            logger.warn("Couldn't find NSA channel (do I have read permission?)");
            return;
        }

        if (!textChannel.canTalk()) {
            logger.warn("Found the NSA channel but don't have talk permissions..");
            return;
        }

        App.getInstance().getEventBus().register(this);
        logger.info("Outputting to channel {} (Guild: {}) | Event listener registered.",
                textChannel.getName(), textChannel.getGuild().getName());
    }

    @Subscribe
    public void onMemberJoin(MemberJoinEvent event) {
        textChannel.sendMessageFormat("%s | **%s** joined channel __%s__.",
                event.getServer().getType(),
                event.getMember().getName(), event.getJoinedChannel().getName())
                .submit();
    }

    @Subscribe
    public void onMemberLeave(MemberLeaveEvent event) {
        textChannel.sendMessageFormat("%s | **%s** left voice chat.",
                event.getServer().getType(), event.getMember().getName())
                .submit();
    }

    @Subscribe
    public void onMemberMove(MemberMoveEvent event) {
        textChannel.sendMessageFormat("%s | **%s** moved from __%s__ to __%s__.",
                event.getServer().getType(), event.getMember().getName(),
                event.getFromChannel().getName(), event.getToChannel().getName())
                .submit();
    }

}
