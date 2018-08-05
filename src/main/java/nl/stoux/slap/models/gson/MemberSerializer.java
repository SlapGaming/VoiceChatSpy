package nl.stoux.slap.models.gson;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import nl.stoux.slap.models.Channel;
import nl.stoux.slap.models.Member;

import java.lang.reflect.Type;

public class MemberSerializer implements JsonSerializer<Member> {

    @Override
    public JsonElement serialize(Member member, Type type, JsonSerializationContext jsonSerializationContext) {
        JsonObject root = new JsonObject();
        root.addProperty("id", member.getIdentifier());
        root.addProperty("name", member.getName());
        root.addProperty("muted", member.isMuted());
        root.addProperty("deafened", member.isDeafened());
        root.addProperty("microphoneDisabled", member.isMicrophoneDisabled());
        return root;
    }
    
}
