package priv.seventeen.artist.orbit.loader.component;

import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.databind.DeserializationContext;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.deser.std.StdDeserializer;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class OutlinerDeserializer extends StdDeserializer<Outliner> {

    public OutlinerDeserializer() {
        super(Outliner.class);
    }

    @Override
    public Outliner deserialize(JsonParser p, DeserializationContext ctxt) throws IOException {
        JsonNode node = p.getCodec().readTree(p);

        if (node.isTextual()) {
            return Outliner.redirect(UUID.fromString(node.asText()));
        }

        UUID uuid = UUID.fromString(node.get("uuid").asText());

        boolean isV5 = !node.has("name") && node.has("uuid");

        if (isV5) {
            List<Outliner> children = deserializeChildren(p, ctxt, node);
            return Outliner.placeholder(uuid, children);
        }

        String name = node.get("name").asText();
        int color = node.has("color") ? node.get("color").asInt() : 0;
        boolean export = getBool(node, "export");
        boolean mirrorUV = getBool(node, "mirror_uv");
        String nbt = node.has("nbt") ? node.get("nbt").asText() : null;
        float[] origin = node.has("origin") ? parseFloatArray(node.get("origin")) : new float[]{0, 0, 0};
        float[] rotation = node.has("rotation") ? parseFloatArray(node.get("rotation")) : new float[]{0, 0, 0};
        boolean visible = !node.has("visibility") && !node.has("visible") || getBool(node, "visibility") || getBool(node, "visible");

        List<Outliner> children = deserializeChildren(p, ctxt, node);

        return new Outliner(uuid, name, color, export, mirrorUV, nbt, origin, rotation, visible, children);
    }

    private List<Outliner> deserializeChildren(JsonParser p, DeserializationContext ctxt, JsonNode node) throws IOException {
        List<Outliner> children = new ArrayList<>();
        if (node.has("children")) {
            JsonNode childrenNode = node.get("children");
            for (int i = 0; i < childrenNode.size(); i++) {
                JsonParser childParser = p.getCodec().treeAsTokens(childrenNode.get(i));
                children.add(deserialize(childParser, ctxt));
            }
        }
        return children;
    }

    private boolean getBool(JsonNode node, String key) {
        return node.has(key) && node.get(key).asBoolean();
    }

    private float[] parseFloatArray(JsonNode arrayNode) {
        float[] result = new float[arrayNode.size()];
        for (int i = 0; i < arrayNode.size(); i++) {
            result[i] = (float) arrayNode.get(i).asDouble();
        }
        return result;
    }
}
