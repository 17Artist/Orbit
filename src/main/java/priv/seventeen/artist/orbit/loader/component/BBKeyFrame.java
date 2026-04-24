package priv.seventeen.artist.orbit.loader.component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BBKeyFrame {
    @JsonProperty("uuid")
    private final UUID uniqueId;
    @JsonProperty("channel")
    private final String channel;
    @JsonProperty("time")
    private final float time;
    @JsonProperty("interpolation")
    private final String interpolation;
    @JsonProperty("bezier_linked")
    private final boolean bezierLinked;
    @JsonProperty("bezier_left_time")
    private final float[] bezierLeftTime;
    @JsonProperty("bezier_left_value")
    private final float[] bezierLeftValue;
    @JsonProperty("bezier_right_time")
    private final float[] bezierRightTime;
    @JsonProperty("bezier_right_value")
    private final float[] bezierRightValue;
    @JsonProperty("data_points")
    private final List<DataPoint> dataPoints;

    public BBKeyFrame(
            @JsonProperty("uuid") UUID uniqueId,
            @JsonProperty("channel") String channel,
            @JsonProperty("time") float time,
            @JsonProperty("interpolation") String interpolation,
            @JsonProperty("bezier_linked") boolean bezierLinked,
            @JsonProperty("bezier_left_time") float[] bezierLeftTime,
            @JsonProperty("bezier_left_value") float[] bezierLeftValue,
            @JsonProperty("bezier_right_time") float[] bezierRightTime,
            @JsonProperty("bezier_right_value") float[] bezierRightValue,
            @JsonProperty("data_points") List<DataPoint> dataPoints) {
        this.uniqueId = uniqueId;
        this.channel = channel;
        this.time = time;
        this.interpolation = interpolation != null ? interpolation : "linear";
        this.bezierLinked = bezierLinked;
        this.bezierLeftTime = bezierLeftTime;
        this.bezierLeftValue = bezierLeftValue;
        this.bezierRightTime = bezierRightTime;
        this.bezierRightValue = bezierRightValue;
        this.dataPoints = dataPoints;
    }

    public UUID uniqueId() { return uniqueId; }
    public String channel() { return channel; }
    public float time() { return time; }
    public String interpolation() { return interpolation; }
    public boolean bezierLinked() { return bezierLinked; }
    public float[] bezierLeftTime() { return bezierLeftTime; }
    public float[] bezierLeftValue() { return bezierLeftValue; }
    public float[] bezierRightTime() { return bezierRightTime; }
    public float[] bezierRightValue() { return bezierRightValue; }
    public List<DataPoint> dataPoints() { return dataPoints; }
}
