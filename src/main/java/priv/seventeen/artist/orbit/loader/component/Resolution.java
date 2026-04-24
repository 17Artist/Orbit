package priv.seventeen.artist.orbit.loader.component;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Resolution {
    @JsonProperty("width")
    private final int width;

    @JsonProperty("height")
    private final int height;

    public Resolution(
            @JsonProperty("width") int width,
            @JsonProperty("height") int height) {
        this.width = width;
        this.height = height;
    }

    public int width() { return width; }
    public int height() { return height; }
}
