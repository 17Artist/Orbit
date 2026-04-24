package priv.seventeen.artist.orbit.loader.component;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Metadata {
    @JsonProperty("format_version")
    private final String formatVersion;

    @JsonProperty("model_format")
    private final String modelFormat;

    @JsonProperty("box_uv")
    private final boolean boxUv;

    public Metadata(
            @JsonProperty("format_version") String formatVersion,
            @JsonProperty("model_format") String modelFormat,
            @JsonProperty("box_uv") boolean boxUv) {
        this.formatVersion = formatVersion;
        this.modelFormat = modelFormat;
        this.boxUv = boxUv;
    }

    public String formatVersion() { return formatVersion; }
    public String modelFormat() { return modelFormat; }
    public boolean boxUv() { return boxUv; }
}
