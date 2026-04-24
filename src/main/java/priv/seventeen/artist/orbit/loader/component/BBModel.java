package priv.seventeen.artist.orbit.loader.component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BBModel {
    @JsonProperty("name")
    private final String name;
    @JsonProperty("model_identifier")
    private final String modelIdentifier;
    @JsonProperty("meta")
    private final Metadata metadata;
    @JsonProperty("resolution")
    private final Resolution resolution;
    @JsonProperty("elements")
    private final List<BBElement> elements;
    @JsonProperty("textures")
    private final List<BBTexture> textures;
    @JsonProperty("display")
    private final Map<String, ItemTransform> display;
    @JsonProperty("outliner")
    private final List<Outliner> outliner;
    @JsonProperty("animations")
    private final List<BBAnimation> animations;
    @JsonProperty("groups")
    private final List<BBGroup> groups;
    @JsonProperty("visible_box")
    private final float[] visibleBox;

    public BBModel(
            @JsonProperty("name") String name,
            @JsonProperty("model_identifier") String modelIdentifier,
            @JsonProperty("meta") Metadata metadata,
            @JsonProperty("resolution") Resolution resolution,
            @JsonProperty("elements") List<BBElement> elements,
            @JsonProperty("textures") List<BBTexture> textures,
            @JsonProperty("display") Map<String, ItemTransform> display,
            @JsonProperty("outliner") List<Outliner> outliner,
            @JsonProperty("animations") List<BBAnimation> animations,
            @JsonProperty("groups") List<BBGroup> groups,
            @JsonProperty("visible_box") float[] visibleBox) {
        this.name = name;
        this.modelIdentifier = modelIdentifier;
        this.metadata = metadata;
        this.resolution = resolution;
        this.elements = elements;
        this.textures = textures;
        this.display = display;
        this.outliner = outliner;
        this.animations = animations;
        this.groups = groups;
        this.visibleBox = visibleBox;
    }

    public String name() { return name; }
    public String modelIdentifier() { return modelIdentifier; }
    public Metadata metadata() { return metadata; }
    public Resolution resolution() { return resolution; }
    public List<BBElement> elements() { return elements; }
    public List<BBTexture> textures() { return textures; }
    public Map<String, ItemTransform> display() { return display; }
    public List<Outliner> outliner() { return outliner; }
    public List<BBAnimation> animations() { return animations; }
    public List<BBGroup> groups() { return groups; }
    public float[] visibleBox() { return visibleBox; }

    public boolean isV5Format() {
        if (metadata != null && metadata.formatVersion() != null) {
            try {
                double ver = Double.parseDouble(metadata.formatVersion());
                if (ver >= 5.0) return true;
            } catch (NumberFormatException ignored) {}
        }
        return groups != null && !groups.isEmpty();
    }

    public boolean hasGroups() {
        return groups != null && !groups.isEmpty();
    }

    public boolean isSingleTextureFormat() {
        if (metadata == null || metadata.modelFormat() == null) return false;
        String fmt = metadata.modelFormat();
        return "bedrock".equalsIgnoreCase(fmt) || fmt.startsWith("animated_") || fmt.startsWith("bedrock");
    }
}
