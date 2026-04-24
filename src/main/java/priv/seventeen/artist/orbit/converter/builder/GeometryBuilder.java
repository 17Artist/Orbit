package priv.seventeen.artist.orbit.converter.builder;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import priv.seventeen.artist.orbit.converter.wrapper.WrappedModel;
import priv.seventeen.artist.orbit.converter.wrapper.WrappedOutliner;
import priv.seventeen.artist.orbit.loader.component.BBTexture;

public class GeometryBuilder {

    private static final JsonNodeFactory F = JsonNodeFactory.instance;

    private GeometryBuilder() {}

    public static String buildFromModel(WrappedModel target, int textureID, float[] bounds) {
        ObjectNode root = F.objectNode();
        root.put("format_version", "1.12.0");

        ArrayNode geometries = F.arrayNode();
        ObjectNode geometry = F.objectNode();

        addDescription(geometry, target, textureID, bounds);
        addBones(geometry, target, textureID);

        geometries.add(geometry);
        root.set("minecraft:geometry", geometries);
        return root.toString();
    }

    private static void addDescription(ObjectNode geometry, WrappedModel target, int textureID, float[] bounds) {
        ObjectNode desc = F.objectNode();

        int texW = 0, texH = 0;
        if (textureID != -999 && target.handler().textures() != null
                && textureID >= 0 && textureID < target.handler().textures().size()) {
            BBTexture tex = target.handler().textures().get(textureID);
            texW = tex.uvWidth();
            texH = tex.uvHeight();
        }
        if ((texW <= 0 || texH <= 0) && target.handler().resolution() != null) {
            if (texW <= 0) texW = target.handler().resolution().width();
            if (texH <= 0) texH = target.handler().resolution().height();
        }
        if (texW > 0) desc.put("texture_width", texW);
        if (texH > 0) desc.put("texture_height", texH);

        if (bounds != null && bounds.length >= 3) {
            desc.put("visible_bounds_width", bounds[0]);
            desc.put("visible_bounds_height", bounds[1]);
            ArrayNode offset = F.arrayNode().add(0).add(bounds[2]).add(0);
            desc.set("visible_bounds_offset", offset);
        }

        geometry.set("description", desc);
    }

    private static void addBones(ObjectNode geometry, WrappedModel target, int textureID) {
        ArrayNode bones = F.arrayNode();
        for (WrappedOutliner outliner : target.outliners()) {
            BoneBuilder.recursivelyCreateBones(bones, outliner, textureID, target.handler());
        }
        geometry.set("bones", bones);
    }
}
