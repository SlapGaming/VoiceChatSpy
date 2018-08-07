package nl.stoux.slap.teamspeak.events;

import com.github.theholywaffle.teamspeak3.api.wrapper.Client;
import nl.stoux.slap.App;
import nl.stoux.slap.events.users.MemberUpdateEvent;
import nl.stoux.slap.teamspeak.helpers.ConnectedServer;
import nl.stoux.slap.teamspeak.models.TeamspeakUser;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.util.List;
import java.util.Objects;
import java.util.TimerTask;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.function.Consumer;

public class UserUpdateTask extends TimerTask {

    private final Logger logger;
    private final AtomicBoolean disabled;
    private final ConnectedServer server;

    public UserUpdateTask(ConnectedServer server) {
        this.server = server;
        this.disabled = new AtomicBoolean(true);
        this.logger = LogManager.getLogger(getClass().getSimpleName() + "-S" + server.getServer().getVirtualServerId());
    }

    @Override
    public void run() {
        if (isDisabled()) {
            return;
        }

        server.refreshServerGroups();
        if (isDisabled()) {
            return;
        }

        Updator updator = new Updator();

        List<Client> clients = server.getTs3Query().getApi().getClients();
        for (Client client : clients) {
            if (isDisabled()) {
                return;
            }

            if (client.getType() == 1) {
                // Skip bots
                continue;
            }

            TeamspeakUser user = server.getUser(client.getId());
            if (user == null) {
                logger.warn("No user found with ID {} (known as '{}')", client.getId(), client.getNickname());
                return;
            }

            boolean dirty = updator.reset()
                    .check(user.getNickname(), client.getNickname(), user::setNickname)
                    .check(user.isMuted(), client.isInputMuted(), user::setMuted)
                    .check(user.isDeafened(), client.isOutputMuted(), user::setDeafened)
                    .check(user.isMicrophoneDisabled(), !client.isInputHardware(), user::setMicrophoneDisabled)
                    .check(user.getGroup(), server.buildGroupPrefix(client.getServerGroups()), user::setGroup)
                    .isDirty();

            if (dirty && !isDisabled()) {
                logger.info("Properties of user {} ({}) have changed, seding update.", user.getNickname(), user.getId());
                user.setServerGroups(client.getServerGroups());
                App.post(new MemberUpdateEvent(server.getServer(), user));
            }
        }
    }

    public boolean isDisabled() {
        return disabled.get();
    }

    public void setDisabled(boolean disabled) {
        this.disabled.set(disabled);
    }

    private class Updator {

        private boolean dirty = false;

        <T> Updator check(T currentValue, T newValue, Consumer<T> setterFunc) {
            if (!Objects.equals(currentValue, newValue)) {
                this.dirty = true;
                setterFunc.accept(newValue);
            }
            return this;
        }

        Updator reset() {
            this.dirty = false;
            return this;
        }

        boolean isDirty() {
            return this.dirty;
        }
    }
}
