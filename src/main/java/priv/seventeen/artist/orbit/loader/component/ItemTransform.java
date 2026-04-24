package priv.seventeen.artist.orbit.loader.component;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ItemTransform {
    @JsonProperty("rotation")
    private final float[] rotation;
    @JsonProperty("translation")
    private final float[] translation;
    @JsonProperty("scale")
    private final float[] scale;

    public ItemTransform(
            @JsonProperty("rotation") float[] rotation,
            @JsonProperty("translation") float[] translation,
            @JsonProperty("scale") float[] scale) {
        this.rotation = rotation;
        this.translation = translation;
        this.scale = scale;
    }

    public float[] rotation() { return rotation; }
    public float[] translation() { return translation; }
    public float[] scale() { return scale; }
}
