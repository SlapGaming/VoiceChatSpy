package nl.stoux.slap.ws;

import com.google.common.eventbus.Subscribe;
import com.google.gson.Gson;
import nl.stoux.slap.App;
import nl.stoux.slap.events.base.BaseEvent;
import nl.stoux.slap.events.ServerUpdateEvent;
import nl.stoux.slap.models.Server;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.java_websocket.WebSocket;
import org.java_websocket.handshake.ClientHandshake;
import org.java_websocket.server.WebSocketServer;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

public class WsServer extends WebSocketServer {

    private final Logger logger = LogManager.getLogger(getClass().getName());
    private ConcurrentHashMap<String, Server> servers;
    private Gson gson;

    public WsServer(int port, Gson gson) {
        super(new InetSocketAddress(port));
        servers = new ConcurrentHashMap<>();
        this.gson = gson;
        App.getInstance().getEventBus().register(this);
    }

    @Override
    public void onOpen(WebSocket conn, ClientHandshake handshake) {
        String remote = getRemote(handshake);
        conn.setAttachment(remote);
        this.logger.info("New client connected from IP {}", remote);

        // Send the user all current Servers
        for (Server server : servers.values()) {
            conn.send(gson.toJson(new ServerUpdateEvent(server)));
        }
    }

    @Override
    public void onClose(WebSocket conn, int code, String reason, boolean remote) {
        this.logger.info("Client disconnected: {}", conn.getAttachment().toString());
    }

    @Override
    public void onMessage(WebSocket conn, String message) {
        // We aren't expecting any messages from them (for now)
    }

    @Override
    public void onError(WebSocket conn, Exception ex) {
        // Couldn't care less.
        logger.fatal("Error occurred: {}", ex.getMessage());

    }

    @Override
    public void onStart() {
        // Nothing here either.
        logger.info("Started!");
    }

    private static String getRemote(ClientHandshake ch) {
        if (ch.hasFieldValue("X-Forwarded-For")) {
            return ch.getFieldValue("X-Forwarded-For");
        } else {
            return ch.getFieldValue("Host");
        }
    }

    @Subscribe
    public void onEvent(BaseEvent event) {
        if (event instanceof ServerUpdateEvent) {
            Server server = ((ServerUpdateEvent) event).getServer();
            logger.info("New server: {}", server.getIdentifier());
            servers.put(server.getIdentifier(), server);
        }

        broadcast(gson.toJson(event));
    }

}
