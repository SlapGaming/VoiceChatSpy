package nl.stoux.slap.teamspeak.models;

import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;

public abstract class TeamspeakChannelParent {

    protected List<TeamspeakChannel> children;

    public TeamspeakChannelParent() {
        this.children = new LinkedList<>();
    }

    void removeChild(TeamspeakChannel teamspeakChannel) {
        this.children.remove(teamspeakChannel);
    }

    void addChild(TeamspeakChannel teamspeakChannel) {
        if (teamspeakChannel.getAfter() == 0) {
            children.add(0, teamspeakChannel);
            return;
        }

        ListIterator<TeamspeakChannel> iterator = children.listIterator();
        while(iterator.hasNext()) {
            TeamspeakChannel currentPosition = iterator.next();
            if (currentPosition.getId() == teamspeakChannel.getAfter()) {
                children.add(iterator.nextIndex(), teamspeakChannel);
                return;
            } else if (currentPosition.getAfter() == teamspeakChannel.getId()) {
                iterator.add(teamspeakChannel);
                return;
            }
        }

        children.add(teamspeakChannel);
    }

}
