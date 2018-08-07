package nl.stoux.slap.teamspeak.events;

import com.github.theholywaffle.teamspeak3.api.event.*;
import lombok.AllArgsConstructor;
import nl.stoux.slap.teamspeak.helpers.ConnectedServer;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

@AllArgsConstructor
public class ChannelListener extends TS3EventAdapter {

    private final Logger logger = LogManager.getLogger(getClass().getSimpleName());
    private ConnectedServer connectedServer;

    @Override
    public void onServerEdit(ServerEditedEvent e) {
        logRebuild(e, "");
    }

    @Override
    public void onChannelEdit(ChannelEditedEvent e) {
        logRebuild(e, "ID : {}", e.getChannelId());
    }

    @Override
    public void onChannelCreate(ChannelCreateEvent e) {
        logRebuild(e, "ID : {}", e.getChannelId());
    }

    @Override
    public void onChannelDeleted(ChannelDeletedEvent e) {
        logRebuild(e, "ID : {}", e.getChannelId());
    }

    @Override
    public void onChannelMoved(ChannelMovedEvent e) {
        logRebuild(e, "ID : {}", e.getChannelId());
    }

    private void logRebuild(BaseEvent event, String additionalMsg, Object... objects) {
        logger.info("[TS-{}] {}" + additionalMsg,
                connectedServer.getServer().getVirtualServerId(), event.getClass().getSimpleName(), objects);
        connectedServer.rebuild();
    }



}
