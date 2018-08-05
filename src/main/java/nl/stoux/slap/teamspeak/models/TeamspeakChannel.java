package nl.stoux.slap.teamspeak.models;

import lombok.Getter;
import nl.stoux.slap.models.Channel;
import nl.stoux.slap.models.Member;

import java.util.*;

public class TeamspeakChannel extends TeamspeakChannelParent implements Channel, Comparable<TeamspeakChannel> {

    @Getter
    private int id;

    @Getter
    private String name;


    @Getter
    private int after;
    private Long userLimit;

    private TeamspeakChannelParent parent;
    private List<TeamspeakUser> users;

    public TeamspeakChannel(int id, String name, int after, Long userLimit) {
        this.id = id;
        this.name = name;
        this.after = after;
        this.userLimit = userLimit;
        this.users = new ArrayList<>();
    }

    @Override
    public String getIdentifier() {
        return "TS-CHANNEL:" + id;
    }

    @Override
    public Long getUserLimit() {
        return null;
    }

    @Override
    public List<? extends Member> getMembers() {
        return users;
    }

    @Override
    public List<? extends Channel> getChildChannels() {
        return children;
    }

    public void moveToParent(TeamspeakChannelParent newParent) {
        if (this.parent != null) {
            this.parent.removeChild(this);
        }
        this.parent = newParent;
        this.parent.addChild(this);
    }

    void addUser(TeamspeakUser user) {
        this.users.add(user);
        // TODO: Sort?
    }

    void removeUser(TeamspeakUser user) {
        this.users.remove(user);
    }

    @Override
    public int compareTo(TeamspeakChannel other) {
        return Long.compare(after, other.after);
    }
}
