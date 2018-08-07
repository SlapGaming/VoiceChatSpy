package nl.stoux.slap.teamspeak.helpers;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.TS3EventType;
import com.github.theholywaffle.teamspeak3.api.wrapper.*;
import lombok.Getter;
import nl.stoux.slap.App;
import nl.stoux.slap.events.ServerUpdateEvent;
import nl.stoux.slap.teamspeak.events.ClientEventListener;
import nl.stoux.slap.teamspeak.events.UserUpdateTask;
import nl.stoux.slap.teamspeak.models.TeamspeakChannel;
import nl.stoux.slap.teamspeak.models.TeamspeakUser;
import nl.stoux.slap.teamspeak.models.TeamspeakVirtualServer;
import org.apache.logging.log4j.util.Strings;

import java.util.*;
import java.util.stream.Collectors;

public class ConnectedServer {

    private static final int REFRESH_INTERVAL = 15 * 1000; // Millis

    @Getter
    private TS3Query ts3Query;
    @Getter
    private TeamspeakVirtualServer server;
    private Map<Integer, TeamspeakChannel> channelMap;
    private Map<Integer, TeamspeakUser> userMap;
    private Map<Integer, ServerGroup> groupMap;

    private Timer userRefreshTimer;
    private UserUpdateTask updateTask;

    public ConnectedServer(TS3Query ts3Query, TeamspeakVirtualServer server) {
        this.ts3Query = ts3Query;
        this.server = server;
        this.channelMap = new HashMap<>();
        this.userMap = new HashMap<>();
        this.groupMap = new HashMap<>();

        this.updateTask = new UserUpdateTask(this);
        this.userRefreshTimer = new Timer();
        this.userRefreshTimer.scheduleAtFixedRate(updateTask, REFRESH_INTERVAL, REFRESH_INTERVAL);
    }

    public void rebuild() {
        updateTask.setDisabled(true);
        TS3Api api = ts3Query.getApi();
        VirtualServerInfo serverInfo = api.getServerInfo();

        server = new TeamspeakVirtualServer(serverInfo.getId(), serverInfo.getName(), (long) serverInfo.getMaxClients());
        channelMap = new HashMap<>();

        // Go through all channels twice, first index all channels
        List<Channel> availableChannels = api.getChannels();
        Iterator<Channel> iterator = availableChannels.iterator();
        while (iterator.hasNext()) {
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

        // Get the server groups
        this.refreshServerGroups();

        // Get users
        userMap = new HashMap<>();
        List<Client> clients = api.getClients();
        for (Client client : clients) {
            if (client.getType() == 1) {
                continue; // Skip Query users
            }

            addTeamspeakUser(client);
        }

        App.getInstance().getEventBus().post(new ServerUpdateEvent(this.server));
        this.updateTask.setDisabled(false);
    }

    public void refreshServerGroups() {
        groupMap = ts3Query.getApi().getServerGroups().stream().collect(Collectors.toMap(ServerGroup::getId, s -> s));
    }

    private TeamspeakUser addTeamspeakUser(Client client) {
        int[] serverGroups = client.getServerGroups();
        TeamspeakUser user = new TeamspeakUser(
                client.getId(), client.getNickname(),
                client.isInputMuted(), client.isOutputMuted(), !client.isInputHardware(),
                buildGroupPrefix(serverGroups), serverGroups
        );

        TeamspeakChannel channel = channelMap.get(client.getChannelId());
        user.moveToChannel(channel);
        userMap.put(user.getId(), user);

        return user;
    }

    public String buildGroupPrefix(int[] groups) {
        String groupPrefix = Arrays.stream(groups).mapToObj(groupMap::get)
                .filter(group -> group.getNameMode() == 1)
                .map(g -> "[" + g.getName() + "]")
                .collect(Collectors.joining(""));

        return Strings.isBlank(groupPrefix) ? null : groupPrefix;
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

    /**
     * Disconnect this server from all APIs and tasks.
     */
    public void disconnect() {
        this.updateTask.setDisabled(true);
        this.userRefreshTimer.cancel();
        this.ts3Query.exit();
    }

}
