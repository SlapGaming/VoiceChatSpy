package nl.stoux.slap.discord;

import net.dv8tion.jda.core.AccountType;
import net.dv8tion.jda.core.JDA;
import net.dv8tion.jda.core.JDABuilder;
import net.dv8tion.jda.core.OnlineStatus;
import net.dv8tion.jda.core.entities.Game;
import net.dv8tion.jda.core.entities.Guild;
import net.dv8tion.jda.core.entities.Member;
import net.dv8tion.jda.core.entities.VoiceChannel;
import nl.stoux.slap.App;
import nl.stoux.slap.config.Config;
import nl.stoux.slap.discord.events.VoiceChannelListener;
import nl.stoux.slap.discord.models.DiscordGuild;
import nl.stoux.slap.discord.models.DiscordVoiceChannel;
import nl.stoux.slap.discord.models.DiscordVoiceMember;
import nl.stoux.slap.discord.models.helpers.DiscordListContainer;
import nl.stoux.slap.events.ServerUpdateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.HashMap;
import java.util.Map;

public class DiscordController {

    private final Logger logger = LogManager.getLogger();
    private final JDA jda;

    private DiscordListContainer<DiscordGuild> guilds;
    private Map<Long, DiscordVoiceMember> members;

    public DiscordController(Config config) throws Exception {
        logger.info("Connecting to Discord...");

        this.jda = new JDABuilder(AccountType.BOT)
                .setToken(config.getDiscordToken())
                .setAudioEnabled(false)
                .setGame(Game.playing("Loading..."))
                .setStatus(OnlineStatus.IDLE)
                .addEventListener()
                .addEventListener(new VoiceChannelListener(this))
                .buildBlocking();

        rebuild();
    }

    /**
     * Rebuild the full server structure
     */
    public void rebuild() {
        DiscordListContainer<DiscordGuild> newGuilds = new DiscordListContainer<>();
        Map<Long, DiscordVoiceMember> newMembers = new HashMap<>();
        for (Guild guild : jda.getGuilds()) {
            DiscordGuild discordGuild = new DiscordGuild(guild.getIdLong(), guild.getName());
            newGuilds.add(discordGuild);

            for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
                DiscordVoiceChannel discordVoiceChannel = new DiscordVoiceChannel(voiceChannel.getIdLong(), voiceChannel.getName(), (long) voiceChannel.getUserLimit());
                discordGuild.addVoiceChannel(discordVoiceChannel);

                for (Member member : voiceChannel.getMembers()) {
                    DiscordVoiceMember discordVoiceMember = new DiscordVoiceMember(member.getUser().getIdLong(), member.getNickname());
                    newMembers.put(discordVoiceMember.getId(), discordVoiceMember);
                    discordVoiceChannel.addMember(discordVoiceMember);
                }
            }

            App.getInstance().getEventBus().post(new ServerUpdateEvent(discordGuild));
        }

        this.guilds = newGuilds;
        this.members = newMembers;

        // TODO: Trigger event to WS
    }


}
