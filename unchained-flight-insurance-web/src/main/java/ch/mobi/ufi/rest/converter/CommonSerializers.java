package ch.mobi.ufi.rest.converter;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.*;
import com.fasterxml.jackson.databind.module.SimpleModule;
import org.springframework.util.StringUtils;
import java.io.IOException;
import java.util.Optional;
import java.util.UUID;

public class CommonSerializers {

    private CommonSerializers() {
    }

    public static Module getModule() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(UUID.class, new UUIDDeserializer());
        module.addSerializer(UUID.class, new UUIDSerializer());
        return module;
    }

    public static class UUIDSerializer extends JsonSerializer<UUID> {
        @Override
        public void serialize(UUID uuid, JsonGenerator gen, SerializerProvider serializers) throws IOException {
            if (uuid != null) {
                gen.writeString(uuid.toString());
            }
        }
    }

    public static class UUIDDeserializer extends JsonDeserializer<UUID> {
        @Override
        public UUID deserialize(JsonParser p, DeserializationContext ctx) throws IOException {
            return Optional.ofNullable(p.readValueAs(String.class))
                    .filter(StringUtils::hasText)
                    .map(UUID::fromString)
                    .orElse(null);
        }
    }
}