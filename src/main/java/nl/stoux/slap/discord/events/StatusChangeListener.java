package nl.stoux.slap.discord.events;

import net.dv8tion.jda.core.events.Event;
import net.dv8tion.jda.core.events.StatusChangeEvent;
import net.dv8tion.jda.core.hooks.EventListener;

public class StatusChangeListener implements EventListener {

    @Override
    public void onEvent(Event event) {
        if (event instanceof StatusChangeEvent) {

        }
    }
}
