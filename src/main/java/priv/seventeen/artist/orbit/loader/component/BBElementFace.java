package priv.seventeen.artist.orbit.loader.component;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class BBElementFace {
    @JsonProperty("north")
    private final BBUv north;
    @JsonProperty("east")
    private final BBUv east;
    @JsonProperty("south")
    private final BBUv south;
    @JsonProperty("west")
    private final BBUv west;
    @JsonProperty("up")
    private final BBUv up;
    @JsonProperty("down")
    private final BBUv down;

    public BBElementFace(
            @JsonProperty("north") BBUv north,
            @JsonProperty("east") BBUv east,
            @JsonProperty("south") BBUv south,
            @JsonProperty("west") BBUv west,
            @JsonProperty("up") BBUv up,
            @JsonProperty("down") BBUv down) {
        this.north = north;
        this.east = east;
        this.south = south;
        this.west = west;
        this.up = up;
        this.down = down;
    }

    public BBUv north() { return north; }
    public BBUv east() { return east; }
    public BBUv south() { return south; }
    public BBUv west() { return west; }
    public BBUv up() { return up; }
    public BBUv down() { return down; }

    public Map<String, BBUv> toMap() {
        if (north == null && east == null && south == null && west == null && up == null && down == null) {
            return Collections.emptyMap();
        }
        Map<String, BBUv> map = new HashMap<>();
        if (north != null) map.put("north", north);
        if (south != null) map.put("south", south);
        if (east != null) map.put("east", east);
        if (west != null) map.put("west", west);
        if (up != null) map.put("up", up);
        if (down != null) map.put("down", down);
        return map;
    }
}
