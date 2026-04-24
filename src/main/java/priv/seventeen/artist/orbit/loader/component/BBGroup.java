package priv.seventeen.artist.orbit.loader.component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BBGroup {
    @JsonProperty("uuid")
    private final UUID uniqueId;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("color")
    private final int color;
    @JsonProperty("export")
    private final boolean export;
    @JsonProperty("mirror_uv")
    private final boolean mirrorUV;
    @JsonProperty("nbt")
    private final String nbt;
    @JsonProperty("origin")
    private final float[] origin;
    @JsonProperty("rotation")
    private final float[] rotation;
    @JsonProperty("visibility")
    private final boolean visibility;

    public BBGroup(
            @JsonProperty("uuid") UUID uniqueId,
            @JsonProperty("name") String name,
            @JsonProperty("color") int color,
            @JsonProperty("export") boolean export,
            @JsonProperty("mirror_uv") boolean mirrorUV,
            @JsonProperty("nbt") String nbt,
            @JsonProperty("origin") float[] origin,
            @JsonProperty("rotation") float[] rotation,
            @JsonProperty("visibility") boolean visibility) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.color = color;
        this.export = export;
        this.mirrorUV = mirrorUV;
        this.nbt = nbt;
        this.origin = origin != null ? origin : new float[]{0f, 0f, 0f};
        this.rotation = rotation != null ? rotation : new float[]{0f, 0f, 0f};
        this.visibility = visibility;
    }

    public UUID uniqueId() { return uniqueId; }
    public String name() { return name; }
    public int color() { return color; }
    public boolean export() { return export; }
    public boolean mirrorUV() { return mirrorUV; }
    public String nbt() { return nbt; }
    public float[] origin() { return origin; }
    public float[] rotation() { return rotation; }
    public boolean visibility() { return visibility; }
}
