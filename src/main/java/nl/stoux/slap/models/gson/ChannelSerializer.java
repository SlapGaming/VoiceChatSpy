package nl.stoux.slap.models.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import nl.stoux.slap.models.Channel;

import java.lang.reflect.Type;

public class ChannelSerializer implements JsonSerializer<Channel> {

    @Override
    public JsonElement serialize(Channel channel, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject root = new JsonObject();
        root.addProperty("id", channel.getIdentifier());
        root.addProperty("name", channel.getName());
        root.addProperty("userLimit", channel.getUserLimit());
        root.add("children", jsonSerializationContext.serialize(channel.getChildChannels()));
        root.add("members", jsonSerializationContext.serialize(channel.getMembers()));
        return root;
    }

}
