package priv.seventeen.artist.orbit.loader.component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BBAnimator {
    @JsonProperty("name")
    private final String name;
    @JsonProperty("type")
    private final String type;
    @JsonProperty("keyframes")
    private final List<BBKeyFrame> keyFrames;
    @JsonProperty("rotation_global")
    private final boolean rotationGlobal;
    @JsonProperty("quaternion_interpolation")
    private final boolean quaternionInterpolation;

    public BBAnimator(
            @JsonProperty("name") String name,
            @JsonProperty("type") String type,
            @JsonProperty("keyframes") List<BBKeyFrame> keyFrames,
            @JsonProperty("rotation_global") boolean rotationGlobal,
            @JsonProperty("quaternion_interpolation") boolean quaternionInterpolation) {
        this.name = name;
        this.type = type;
        this.keyFrames = keyFrames;
        this.rotationGlobal = rotationGlobal;
        this.quaternionInterpolation = quaternionInterpolation;
    }

    public String name() { return name; }
    public String type() { return type; }
    public List<BBKeyFrame> keyFrames() { return keyFrames; }
    public boolean rotationGlobal() { return rotationGlobal; }
    public boolean quaternionInterpolation() { return quaternionInterpolation; }
}
