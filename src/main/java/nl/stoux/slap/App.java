package nl.stoux.slap;

import com.google.common.eventbus.EventBus;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import lombok.Getter;
import nl.stoux.slap.config.Config;
import nl.stoux.slap.discord.DiscordController;
import nl.stoux.slap.events.base.BaseEvent;
import nl.stoux.slap.models.Channel;
import nl.stoux.slap.models.Member;
import nl.stoux.slap.models.Server;
import nl.stoux.slap.models.gson.ChannelSerializer;
import nl.stoux.slap.models.gson.MemberSerializer;
import nl.stoux.slap.models.gson.ServerSerializer;
import nl.stoux.slap.teamspeak.TeamspeakController;
import nl.stoux.slap.ws.WsServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class App {

    private static App instance;

    private final Logger logger = LogManager.getLogger();
    @Getter
    private final EventBus eventBus;
    @Getter
    private final Gson gson;

    private final Config config;
    private final DiscordController discord;
    private final TeamspeakController teamspeak;
    private final WsServer websocket;
    
    public App() {
        logger.info("Starting...");
        instance = this;
        eventBus = new EventBus();
        gson = buildGson();

        try {
            config = new Config();
            websocket = new WsServer(config.getWebsocketPort(), gson);
            websocket.start();

            // Wait for the websocket to bind
            while (!websocket.STARTED.get() && !websocket.ERROR.get()) {
                // Not allowed to call wait()?
            }
            if (websocket.ERROR.get()) {
                websocket.stop();
                logger.fatal("Shutting down due to websocket error.");
                throw new RuntimeException("RIP");
            }


            discord = new DiscordController(config);
            teamspeak = new TeamspeakController(config);
        } catch (Exception e) {
            logger.fatal("Error during startup", e);
            throw new RuntimeException("RIP");
        }
    }

    public static App getInstance() {
        return instance;
    }

    /**
     * Post an event to the EventBus.
     *
     * @param event The event
     */
    public static void post(BaseEvent event) {
        getInstance().getEventBus().post(event);
    }

    private Gson buildGson() {
        return new GsonBuilder()
                .registerTypeHierarchyAdapter(Channel.class, new ChannelSerializer())
                .registerTypeHierarchyAdapter(Member.class, new MemberSerializer())
                .registerTypeHierarchyAdapter(Server.class, new ServerSerializer())
                .setPrettyPrinting()
                .serializeNulls()
                .create();
    }

    public void disable() {
        try {
            logger.info("Shutting down websocket...");
            this.websocket.stop();
        } catch (Exception e) {
            logger.warn("Error shutting down Websocket.");
        }

        logger.info("Disconnecting all TS3 servers...");
        this.teamspeak.disconnect();

        logger.info("Disconnecting from Discord");
        this.discord.disconnect();
    }
}
