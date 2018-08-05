package nl.stoux.slap.models;

public interface Member {

    String getIdentifier();

    String getName();

    boolean isMuted();

    boolean isMicrophoneDisabled();

    boolean isDeafened();

}
