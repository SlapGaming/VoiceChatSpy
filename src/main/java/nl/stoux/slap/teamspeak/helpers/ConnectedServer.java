package nl.stoux.slap.teamspeak.helpers;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.wrapper.*;
import lombok.Getter;
import nl.stoux.slap.App;
import nl.stoux.slap.events.ServerUpdateEvent;
import nl.stoux.slap.events.users.MemberJoinEvent;
import nl.stoux.slap.teamspeak.events.ClientEventListener;
import nl.stoux.slap.teamspeak.models.TeamspeakChannel;
import nl.stoux.slap.teamspeak.models.TeamspeakUser;
import nl.stoux.slap.teamspeak.models.TeamspeakVirtualServer;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;

public class ConnectedServer {

    private TS3Query ts3Query;
    @Getter
    private TeamspeakVirtualServer server;
    private Map<Integer, TeamspeakChannel> channelMap;
    private Map<Integer, TeamspeakUser> userMap;

    public ConnectedServer(TS3Query ts3Query, TeamspeakVirtualServer server) {
        this.ts3Query = ts3Query;
        this.server = server;
        this.channelMap = new HashMap<>();
        this.userMap = new HashMap<>();
    }

    public void rebuild() {
        TS3Api api = ts3Query.getApi();
        VirtualServerInfo serverInfo = api.getServerInfo();

        server = new TeamspeakVirtualServer(serverInfo.getId(), serverInfo.getName(), (long) serverInfo.getMaxClients());
        channelMap = new HashMap<>();

        // Go through all channels twice, first index all channels
        List<Channel> availableChannels = api.getChannels();
        Iterator<Channel> iterator = availableChannels.iterator();
        while(iterator.hasNext()) {
            Channel availableChannel = iterator.next();
            TeamspeakChannel teamspeakChannel = new TeamspeakChannel(availableChannel.getId(), availableChannel.getName(),
                    availableChannel.getOrder(), (long) availableChannel.getMaxClients());
            channelMap.put(teamspeakChannel.getId(), teamspeakChannel);
            if (availableChannel.getParentChannelId() == 0) {
                teamspeakChannel.moveToParent(server);
                iterator.remove();
            }
        }

        // Go through all children that don't have a parent yet
        for (Channel availableChannel : availableChannels) {
            TeamspeakChannel teamspeakChannel = channelMap.get(availableChannel.getId());
            TeamspeakChannel parentTeamspeakChannel = channelMap.get(availableChannel.getParentChannelId());
            teamspeakChannel.moveToParent(parentTeamspeakChannel);
        }

        // Get users
        userMap = new HashMap<>();
        List<Client> clients = api.getClients();
        for (Client client : clients) {
            if (client.getType() == 1) {
                continue; // Skip Query users
            }

            addTeamspeakUser(client);

            // TODO: User groups
        }

        App.getInstance().getEventBus().post(new ServerUpdateEvent(this.server));
    }

    private TeamspeakUser addTeamspeakUser(Client client) {
        TeamspeakUser user = new TeamspeakUser(
                client.getId(), client.getNickname(),
                client.isInputMuted(), client.isOutputMuted(), !client.isInputHardware()
        );

        TeamspeakChannel channel = channelMap.get(client.getChannelId());
        user.moveToChannel(channel);
        userMap.put(user.getId(), user);

        return user;
    }

    public static ConnectedServer init(TS3Query query, VirtualServer virtualServer) {
        TS3Api api = query.getApi();
        api.selectVirtualServerById(virtualServer.getId());

        // Build the server data
        TeamspeakVirtualServer tsServer = new TeamspeakVirtualServer(virtualServer.getId(), virtualServer.getName(), (long) virtualServer.getMaxClients());
        ConnectedServer connectedServer = new ConnectedServer(query, tsServer);

        connectedServer.rebuild();

        // Register the listeners
        api.registerEvents(TS3EventType.SERVER, TS3EventType.CHANNEL);
        api.addTS3Listeners(new ClientEventListener(connectedServer));

        return connectedServer;
    }

    public TeamspeakChannel getChannel(int id) {
        return this.channelMap.get(id);
    }

    public TeamspeakUser getUser(int id) {
        return this.userMap.get(id);
    }

    public TeamspeakUser onUserJoin(String uniqueIdentifier) {
        ClientInfo client = ts3Query.getApi().getClientByUId(uniqueIdentifier);
        return this.addTeamspeakUser(client);
    }

}
