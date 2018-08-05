package nl.stoux.slap.models;

import java.util.List;

public interface Channel {

    String getIdentifier();

    String getName();

    Long getUserLimit();

    List<? extends Member> getMembers();

    List<? extends Channel> getChildChannels();


}
