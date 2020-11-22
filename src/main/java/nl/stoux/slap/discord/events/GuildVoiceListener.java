package nl.stoux.slap.discord.events;

import net.dv8tion.jda.api.entities.GuildVoiceState;
import net.dv8tion.jda.api.entities.Member;
import net.dv8tion.jda.api.entities.User;
import net.dv8tion.jda.api.entities.VoiceChannel;
import net.dv8tion.jda.api.events.guild.voice.GenericGuildVoiceEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceJoinEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceLeaveEvent;
import net.dv8tion.jda.api.events.guild.voice.GuildVoiceMoveEvent;
import net.dv8tion.jda.api.hooks.ListenerAdapter;
import nl.stoux.slap.App;
import nl.stoux.slap.discord.DiscordController;
import nl.stoux.slap.discord.models.DiscordGuild;
import nl.stoux.slap.discord.models.DiscordVoiceChannel;
import nl.stoux.slap.discord.models.DiscordVoiceMember;
import nl.stoux.slap.events.users.MemberJoinEvent;
import nl.stoux.slap.events.users.MemberLeaveEvent;
import nl.stoux.slap.events.users.MemberMoveEvent;
import nl.stoux.slap.events.users.MemberUpdateEvent;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class GuildVoiceListener extends ListenerAdapter {

    private final Logger logger;
    private final DiscordController discord;

    public GuildVoiceListener(DiscordController discord) {
        this.discord = discord;
        this.logger = LogManager.getLogger(getClass().getName());
    }


    @Override
    public void onGuildVoiceJoin(GuildVoiceJoinEvent event) {
        DiscordGuild guild = getGuild(event);
        DiscordVoiceChannel channel = getChannel(guild, event.getChannelJoined());
        DiscordVoiceMember member = discord.createMember(event.getMember(), channel);

        this.logger.info("User {} has joined channel {} of guild {}",
                member.getName(), channel.getName(), guild.getName());

        App.post(new MemberJoinEvent(guild, member, channel));
    }

    @Override
    public void onGuildVoiceLeave(GuildVoiceLeaveEvent event) {
        DiscordGuild guild = getGuild(event);
        DiscordVoiceChannel channel = getChannel(guild, event.getChannelLeft());
        DiscordVoiceMember member = getMember(event, channel);

        channel.removeMember(member);

        this.logger.info("User {} has left the voice channels of guild {}", member.getName(), guild.getName());

        App.post(new MemberLeaveEvent(guild, member));
    }

    @Override
    public void onGuildVoiceMove(GuildVoiceMoveEvent event) {
        DiscordGuild guild = getGuild(event);
        DiscordVoiceChannel oldChannel = getChannel(guild, event.getChannelLeft());
        DiscordVoiceChannel newChannel = getChannel(guild, event.getChannelJoined());
        DiscordVoiceMember member = getMember(event, oldChannel);

        oldChannel.removeMember(member);
        newChannel.addMember(member);

        this.logger.info("User {} moved from {} to {} in guild {}",
                member.getName(), oldChannel.getName(), newChannel.getName(), guild.getName());

        App.post(new MemberMoveEvent(guild, member, oldChannel, newChannel));
    }

    // Property updates

    @Override
    public void onGenericGuildVoice(GenericGuildVoiceEvent event) {
        if (event instanceof GuildVoiceJoinEvent || event instanceof GuildVoiceMoveEvent || event instanceof GuildVoiceLeaveEvent) {
            return;
        }

        Member eventMember = event.getMember();
        User eventUser = eventMember.getUser();
        GuildVoiceState voiceState = eventMember.getVoiceState();

        DiscordGuild guild = discord.getGuild(event.getGuild().getIdLong());
        if (voiceState == null || voiceState.getChannel() == null) {
            logger.debug("Voice state for user {} ignored due to no channel being set", eventMember.getEffectiveName());
            return;
        }


        DiscordVoiceChannel channel = getChannel(guild, voiceState.getChannel());
        DiscordVoiceMember member = getMember(event, channel);

        member.setName(eventUser.getName());
        member.setVoiceState(voiceState);

        logger.info("Updated voice state of user {} on guild {}", member.getName(), guild.getName());

        App.post(new MemberUpdateEvent(guild, member));
    }

    private DiscordGuild getGuild(GenericGuildVoiceEvent event) {
        return discord.getGuild(event.getGuild().getIdLong());
    }

    private DiscordVoiceChannel getChannel(DiscordGuild guild, VoiceChannel voiceChannel) {
        return guild.getVoiceChannel(voiceChannel.getIdLong());
    }

    private DiscordVoiceMember getMember(GenericGuildVoiceEvent event, DiscordVoiceChannel channel) {
        return channel.getMember(event.getMember().getUser().getIdLong());
    }

}
