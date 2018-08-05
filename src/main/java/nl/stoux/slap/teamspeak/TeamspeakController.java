package nl.stoux.slap.teamspeak;

import com.github.theholywaffle.teamspeak3.TS3Api;
import com.github.theholywaffle.teamspeak3.TS3Config;
import com.github.theholywaffle.teamspeak3.TS3Query;
import com.github.theholywaffle.teamspeak3.api.event.*;
import com.github.theholywaffle.teamspeak3.api.exception.TS3CommandFailedException;
import com.github.theholywaffle.teamspeak3.api.exception.TS3Exception;
import com.github.theholywaffle.teamspeak3.api.wrapper.VirtualServer;
import lombok.AllArgsConstructor;
import nl.stoux.slap.config.Config;
import nl.stoux.slap.teamspeak.events.ClientEventListener;
import nl.stoux.slap.teamspeak.helpers.ConnectedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.ArrayList;
import java.util.List;

public class TeamspeakController {

    private final Logger logger = LogManager.getLogger(getClass().getSimpleName());

    private List<ConnectedServer> servers;
    private Config appConfig;
    private TS3Config ts3Config;

    public TeamspeakController(Config config) {
        this.servers = new ArrayList<>();
        this.appConfig = config;

        // Build the TS3 config
        ts3Config = new TS3Config();
        ts3Config.setFloodRate(TS3Query.FloodRate.UNLIMITED);
        ts3Config.setHost(config.getTeamspeakServer());
        ts3Config.setQueryPort(config.getTeamspeakPort());

        detectServers();
    }

    private void detectServers() {
        TS3Query ts3Query = new TS3Query(ts3Config);
        logger.info("Connecting to Teamspeak");
        this.connect(ts3Query);

        // Fetch the available server
        TS3Api api = ts3Query.getApi();
        for (VirtualServer virtualServer : api.getVirtualServers()) {
            logger.info("Connecting to server: " + virtualServer.getId());
            TS3Query serverQuery;
            if (servers.isEmpty()) {
                // Use the current query
                serverQuery = ts3Query;
            } else {
                serverQuery = new TS3Query(ts3Config);
                this.connect(serverQuery);
            }

            servers.add(ConnectedServer.init(serverQuery, virtualServer));
            logger.info("Connected.");
        }
    }

    private void connect(TS3Query ts3Query) {
        try {
            if (!ts3Query.isConnected()) {
                ts3Query.connect();
            }
            TS3Api api = ts3Query.getApi();
            api.login(appConfig.getTeamspeakUser(), appConfig.getTeamspeakPassword());
        } catch (TS3Exception e) {
            logger.fatal("Failed to connect to Teampspeak: {}", e);
            throw e;
        }
    }

}
