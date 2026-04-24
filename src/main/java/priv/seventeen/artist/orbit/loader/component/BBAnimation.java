package priv.seventeen.artist.orbit.loader.component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Map;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BBAnimation {
    @JsonProperty("uuid")
    private final String uuid;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("loop")
    private final String loop;
    @JsonProperty("override")
    private final boolean override;
    @JsonProperty("length")
    private final float length;
    @JsonProperty("snapping")
    private final int snapping;
    @JsonProperty("animators")
    private final Map<String, BBAnimator> animators;

    public BBAnimation(
            @JsonProperty("uuid") String uuid,
            @JsonProperty("name") String name,
            @JsonProperty("loop") String loop,
            @JsonProperty("override") boolean override,
            @JsonProperty("length") float length,
            @JsonProperty("snapping") int snapping,
            @JsonProperty("animators") Map<String, BBAnimator> animators) {
        this.uuid = uuid;
        this.name = name;
        this.loop = loop;
        this.override = override;
        this.length = length;
        this.snapping = snapping;
        this.animators = animators;
    }

    public String uuid() { return uuid; }
    public String name() { return name; }
    public String loop() { return loop; }
    public boolean override() { return override; }
    public float length() { return length; }
    public int snapping() { return snapping; }
    public Map<String, BBAnimator> animators() { return animators; }
}
