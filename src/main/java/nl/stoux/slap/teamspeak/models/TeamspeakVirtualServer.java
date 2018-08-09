package nl.stoux.slap.teamspeak.models;

import lombok.Getter;
import nl.stoux.slap.models.Channel;
import nl.stoux.slap.models.Server;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class TeamspeakVirtualServer extends TeamspeakChannelParent implements Server {

    @Getter
    private int virtualServerId;
    @Getter
    private String name;
    private Long userLimit;

    public TeamspeakVirtualServer(int virtualServerId, String name, Long userLimit) {
        this.virtualServerId = virtualServerId;
        this.name = name;
        this.userLimit = userLimit;
    }

    @Override
    public String getIdentifier() {
        return "TS-SERVER:" + this.virtualServerId;
    }

    @Override
    public String getType() {
        return "TS";
    }

    @Override
    public List<? extends Channel> getChannels() {
        return children;
    }

    @Override
    public Long getUsersOnline() {
        return null;
    }

    @Override
    public Long getMaxUsers() {
        return userLimit;
    }

}
