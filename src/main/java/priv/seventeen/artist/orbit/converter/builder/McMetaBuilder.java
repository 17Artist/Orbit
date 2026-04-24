package priv.seventeen.artist.orbit.converter.builder;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import priv.seventeen.artist.orbit.loader.component.BBTexture;

public class McMetaBuilder {

    private static final JsonNodeFactory F = JsonNodeFactory.instance;

    private McMetaBuilder() {}

    public static String createAnimationMeta(BBTexture texture) {
        if (texture == null || !texture.isAnimated()) return null;

        ObjectNode root = F.objectNode();
        ObjectNode animation = F.objectNode();

        animation.put("frametime", texture.frameTime() > 0 ? texture.frameTime() : 1);

        if (texture.uvWidth() != texture.uvHeight()) {
            animation.put("width", texture.uvWidth());
            animation.put("height", texture.uvHeight());
        }

        if (texture.frameInterpolate()) {
            animation.put("interpolate", true);
        }

        ArrayNode frames = buildFrameOrder(texture);
        if (frames != null) {
            animation.set("frames", frames);
        }

        root.set("animation", animation);
        return root.toString();
    }

    private static ArrayNode buildFrameOrder(BBTexture texture) {
        int frameCount = texture.frameCount();
        if (frameCount <= 1) return null;

        String type = texture.frameOrderType();
        if (type == null || "loop".equals(type)) return null;

        ArrayNode frames = F.arrayNode();

        if ("backwards".equals(type)) {
            for (int i = frameCount - 1; i >= 0; i--) frames.add(i);
            return frames;
        }

        if ("back_and_forth".equals(type)) {
            for (int i = 0; i < frameCount; i++) frames.add(i);
            for (int i = frameCount - 2; i > 0; i--) frames.add(i);
            return frames;
        }

        if ("custom".equals(type) && texture.frameOrder() != null && !texture.frameOrder().trim().isEmpty()) {
            String[] parts = texture.frameOrder().trim().split("\\s+");
            for (String part : parts) {
                if (part.contains(":")) {
                    String[] split = part.split(":");
                    try {
                        ObjectNode entry = F.objectNode();
                        entry.put("index", Integer.parseInt(split[0]));
                        entry.put("time", Integer.parseInt(split[1]));
                        frames.add(entry);
                    } catch (NumberFormatException ignored) {}
                } else {
                    try { frames.add(Integer.parseInt(part)); }
                    catch (NumberFormatException ignored) {}
                }
            }
            return !frames.isEmpty() ? frames : null;
        }

        return null;
    }
}
