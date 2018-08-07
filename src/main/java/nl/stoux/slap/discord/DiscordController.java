package nl.stoux.slap.discord;

import com.jagrosh.jdautilities.command.CommandClient;
import com.jagrosh.jdautilities.command.CommandClientBuilder;
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
import nl.stoux.slap.discord.commands.ShutdownCommand;
import nl.stoux.slap.discord.events.GuildVoiceListener;
import nl.stoux.slap.discord.events.VoiceChannelListener;
import nl.stoux.slap.discord.models.DiscordGuild;
import nl.stoux.slap.discord.models.DiscordVoiceChannel;
import nl.stoux.slap.discord.models.DiscordVoiceMember;
import nl.stoux.slap.discord.models.helpers.DiscordListContainer;
import nl.stoux.slap.events.ServerUpdateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class DiscordController {

    private final Logger logger = LogManager.getLogger();
    private final JDA jda;

    private DiscordListContainer<DiscordGuild> guilds;

    public DiscordController(Config config) throws Exception {
        logger.info("Connecting to Discord...");

        CommandClient commandClient = new CommandClientBuilder()
                .useHelpBuilder(false)
                .setOwnerId(config.getDiscordOwner())
                .addCommand(new ShutdownCommand())
                .build();

        this.jda = new JDABuilder(AccountType.BOT)
                .setToken(config.getDiscordToken())
                .setAudioEnabled(false)
                .setGame(Game.watching("you"))
                .setStatus(OnlineStatus.ONLINE)
                .addEventListener(commandClient)
                .addEventListener(new GuildVoiceListener(this))
                .addEventListener(new VoiceChannelListener(this))
                .buildBlocking();

        build();
    }

    /**
     * Rebuild the full server structure
     */
    public void build() {
        guilds = new DiscordListContainer<>();

        for (Guild guild : jda.getGuilds()) {
            DiscordGuild discordGuild = new DiscordGuild(guild.getIdLong(), guild.getName());
            guilds.add(discordGuild);
            fillGuild(guild, discordGuild);
        }
    }

    private void fillGuild(Guild guild, DiscordGuild discordGuild) {
        for (VoiceChannel voiceChannel : guild.getVoiceChannels()) {
            DiscordVoiceChannel discordVoiceChannel = new DiscordVoiceChannel(voiceChannel.getIdLong(), voiceChannel.getName(), (long) voiceChannel.getUserLimit());
            discordGuild.addVoiceChannel(discordVoiceChannel);

            for (Member member : voiceChannel.getMembers()) {
                createMember(member, discordVoiceChannel);
            }
        }

        App.post(new ServerUpdateEvent(discordGuild));
    }

    /**
     * Rebuild a single guild.
     *
     * @param guildId
     */
    public void rebuildGuild(long guildId) {
        // Remove the old one
        DiscordGuild item = guilds.getItem(guildId);
        if (item != null) {
            guilds.remove(item);
        }

        // Build a new one
        Guild guild = jda.getGuildById(guildId);
        DiscordGuild discordGuild = new DiscordGuild(guild.getIdLong(), guild.getName());
        guilds.add(discordGuild);
        fillGuild(guild, discordGuild);
    }

    public DiscordGuild getGuild(long id) {
        return guilds.getItem(id);
    }

    public DiscordVoiceMember createMember(Member member, DiscordVoiceChannel addToChannel) {
        DiscordVoiceMember discordVoiceMember
                = new DiscordVoiceMember(member.getUser().getIdLong(), member.getEffectiveName(), member.getVoiceState());
        addToChannel.addMember(discordVoiceMember);
        return discordVoiceMember;
    }

    /**
     * Disconnect the bot from Discord.
     */
    public void disconnect() {
        this.jda.shutdown();
    }

}
