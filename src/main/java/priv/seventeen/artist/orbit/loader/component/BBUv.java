package priv.seventeen.artist.orbit.loader.component;

import com.fasterxml.jackson.annotation.JsonProperty;

public class BBUv {
    @JsonProperty("uv")
    private final float[] uv;

    @JsonProperty("texture")
    private final int texture;

    @JsonProperty("rotation")
    private final int rotation;

    public BBUv(
            @JsonProperty("uv") float[] uv,
            @JsonProperty("texture") int texture,
            @JsonProperty("rotation") int rotation) {
        this.uv = uv;
        this.texture = texture;
        this.rotation = rotation;
    }

    public float[] uv() { return uv; }
    public int texture() { return texture; }
    public int rotation() { return rotation; }

    public boolean hasTexture() {
        return texture >= 0;
    }
}
