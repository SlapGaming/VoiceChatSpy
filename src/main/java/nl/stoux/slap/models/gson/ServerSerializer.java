package nl.stoux.slap.models.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import nl.stoux.slap.models.Channel;
import nl.stoux.slap.models.Server;

import java.lang.reflect.Type;

public class ServerSerializer implements JsonSerializer<Server> {

    @Override
    public JsonElement serialize(Server server, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject root = new JsonObject();
        root.addProperty("id", server.getIdentifier());
        root.addProperty("name", server.getName());
        root.addProperty("type", server.getType());
        root.addProperty("maxUsers", server.getMaxUsers());
        root.add("channels", jsonSerializationContext.serialize(server.getChannels()));
        return root;
    }

}
