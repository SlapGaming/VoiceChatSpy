package nl.stoux.slap.models;

import java.util.List;

public interface Server {

    String getIdentifier();

    String getName();

    String getType();

    List<? extends Channel> getChannels();

    Long getUsersOnline();

    Long getMaxUsers();

}
