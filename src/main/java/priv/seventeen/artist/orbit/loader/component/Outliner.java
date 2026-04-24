package priv.seventeen.artist.orbit.loader.component;

import com.fasterxml.jackson.databind.annotation.JsonDeserialize;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@JsonDeserialize(using = OutlinerDeserializer.class)
public class Outliner {
    private final UUID uniqueId;
    private final String name;
    private final int color;
    private final boolean export;
    private final boolean mirrorUV;
    private final String nbt;
    private final float[] origin;
    private final float[] rotation;
    private final boolean visible;
    private final List<Outliner> children;

    public Outliner(UUID uniqueId, String name, int color, boolean export,
                    boolean mirrorUV, String nbt, float[] origin, float[] rotation,
                    boolean visible, List<Outliner> children) {
        this.uniqueId = uniqueId;
        this.name = name;
        this.color = color;
        this.export = export;
        this.mirrorUV = mirrorUV;
        this.nbt = nbt;
        this.origin = origin;
        this.rotation = rotation;
        this.visible = visible;
        this.children = children != null ? children : new ArrayList<>();
    }

    public UUID uniqueId() { return uniqueId; }
    public String name() { return name; }
    public int color() { return color; }
    public boolean export() { return export; }
    public boolean mirrorUV() { return mirrorUV; }
    public String nbt() { return nbt; }
    public float[] origin() { return origin; }
    public float[] rotation() { return rotation; }
    public boolean visible() { return visible; }
    public List<Outliner> children() { return children; }

    public boolean isRedirection() { return name == null; }

    public static Outliner redirect(UUID uniqueId) {
        return new Outliner(uniqueId, null, 0, false, false, null,
                new float[]{0, 0, 0}, new float[]{0, 0, 0}, true, new ArrayList<>());
    }

    public static Outliner placeholder(UUID uniqueId, List<Outliner> children) {
        return new Outliner(uniqueId, null, 0, true, false, null,
                new float[]{0, 0, 0}, new float[]{0, 0, 0}, true, children);
    }

    public Outliner fillFromGroup(BBGroup group) {
        if (group == null) return this;
        return new Outliner(uniqueId, group.name(), group.color(), group.export(),
                group.mirrorUV(), group.nbt(), group.origin(), group.rotation(),
                group.visibility(), children);
    }

    public static Outliner createRoot(List<Outliner> children) {
        return new Outliner(UUID.randomUUID(), "bb_main", 0, false, false, null,
                new float[]{0, 0, 0}, new float[]{0, 0, 0}, true, children);
    }
}
