package priv.seventeen.artist.orbit.loader.component;

import com.fasterxml.jackson.annotation.JsonProperty;

public class DataPoint {
    @JsonProperty("x")
    private final String x;
    @JsonProperty("y")
    private final String y;
    @JsonProperty("z")
    private final String z;
    @JsonProperty("effect")
    private final String effect;
    @JsonProperty("locator")
    private final String locator;
    @JsonProperty("script")
    private final String script;

    public DataPoint(
            @JsonProperty("x") String x,
            @JsonProperty("y") String y,
            @JsonProperty("z") String z,
            @JsonProperty("effect") String effect,
            @JsonProperty("locator") String locator,
            @JsonProperty("script") String script) {
        this.x = x;
        this.y = y;
        this.z = z;
        this.effect = effect;
        this.locator = locator;
        this.script = script;
    }

    public String x() { return x; }
    public String y() { return y; }
    public String z() { return z; }
    public String effect() { return effect; }
    public String locator() { return locator; }
    public String script() { return script; }

    public String xAsString() { return isNullOrEmpty(x) ? "0" : x; }
    public String yAsString() { return isNullOrEmpty(y) ? "0" : y; }
    public String zAsString() { return isNullOrEmpty(z) ? "0" : z; }

    public float xAsFloat() {
        if (isNullOrEmpty(x)) return 0f;
        try { return Float.parseFloat(x); }
        catch (NumberFormatException e) { return 0f; }
    }

    public float yAsFloat() {
        if (isNullOrEmpty(y)) return 0f;
        try { return Float.parseFloat(y); }
        catch (NumberFormatException e) { return 0f; }
    }

    public float zAsFloat() {
        if (isNullOrEmpty(z)) return 0f;
        try { return Float.parseFloat(z); }
        catch (NumberFormatException e) { return 0f; }
    }

    private boolean isNullOrEmpty(String str) {
        return str == null || str.isEmpty();
    }
}
