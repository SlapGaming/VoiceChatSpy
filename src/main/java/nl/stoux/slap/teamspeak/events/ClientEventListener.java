package nl.stoux.slap.teamspeak.events;

import com.github.theholywaffle.teamspeak3.api.event.ClientJoinEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientLeaveEvent;
import com.github.theholywaffle.teamspeak3.api.event.ClientMovedEvent;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventAdapter;
import lombok.AllArgsConstructor;
import nl.stoux.slap.App;
import nl.stoux.slap.events.users.MemberJoinEvent;
import nl.stoux.slap.events.users.MemberLeaveEvent;
import nl.stoux.slap.events.users.MemberMoveEvent;
import nl.stoux.slap.models.Server;
import nl.stoux.slap.teamspeak.helpers.ConnectedServer;
import nl.stoux.slap.teamspeak.models.TeamspeakChannel;
import nl.stoux.slap.teamspeak.models.TeamspeakUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AllArgsConstructor
public class ClientEventListener extends TS3EventAdapter {

    private final Logger logger = LogManager.getLogger(getClass().getSimpleName());

    private ConnectedServer connectedServer;

    @Override
    public void onClientJoin(ClientJoinEvent e) {
        if (e.getClientType() == 1) {
            return;
        }
        TeamspeakUser user = connectedServer.onUserJoin(e.getUniqueClientIdentifier());
        logger.info("Client joined: {} ({})", user.getNickname(), user.getId());
        App.post(new MemberJoinEvent(
                getServer(), user, user.getChannel()
        ));
    }

    @Override
    public void onClientLeave(ClientLeaveEvent e) {
        TeamspeakUser user = connectedServer.getUser(e.getClientId());
        if (user == null) {
            return;
        }

        logger.info("Client left: {} ({})", user.getNickname(), user.getId());
        user.moveToChannel(null);
        App.post(new MemberLeaveEvent(getServer(), user));
    }

    @Override
    public void onClientMoved(ClientMovedEvent e) {
        TeamspeakUser user = connectedServer.getUser(e.getClientId());
        if (user == null) {
            return;
        }

        TeamspeakChannel fromChannel = user.getChannel();
        TeamspeakChannel toChannel = connectedServer.getChannel(e.getTargetChannelId());
        user.moveToChannel(toChannel);

        logger.info("Client moved '{}' ({}) moved from {} to {}",
                user.getNickname(), user.getId(),
                fromChannel.getName(), toChannel.getName());

        App.post(new MemberMoveEvent(getServer(), user, fromChannel, toChannel));
    }

    private Server getServer() {
        return connectedServer.getServer();
    }


}
