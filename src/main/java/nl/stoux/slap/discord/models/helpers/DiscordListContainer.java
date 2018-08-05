package nl.stoux.slap.discord.models.helpers;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class DiscordListContainer<T extends DiscordId> {

    private final HashMap<Long, T> items;
    private final List<T> list;

    public DiscordListContainer() {
        items = new HashMap<>();
        list = new ArrayList<>();
    }

    public void add(T item) {
        items.put(item.getId(), item);
        list.add(item);
    }

    public void remove(T item) {
        items.remove(item.getId());
        list.remove(item);
    }

    public T getItem(Long id) {
        return items.get(id);
    }

    public List<T> getList() {
        return list;
    }
}
