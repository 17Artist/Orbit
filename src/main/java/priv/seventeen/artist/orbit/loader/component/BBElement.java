package priv.seventeen.artist.orbit.loader.component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BBElement {
    @JsonProperty("uuid")
    private final UUID uniqueId;
    @JsonProperty("type")
    private final String type;
    @JsonProperty("name")
    private final String name;
    @JsonProperty("origin")
    private final float[] origin;
    @JsonProperty("rotation")
    private final float[] rotation;

    // Cube fields
    @JsonProperty("box_uv")
    private final boolean boxUv;
    @JsonProperty("mirror_uv")
    private final boolean mirrorUv;
    @JsonProperty("inflate")
    private final double inflate;
    @JsonProperty("from")
    private final float[] from;
    @JsonProperty("to")
    private final float[] to;
    @JsonProperty("uv_offset")
    private final float[] uvOffset;
    @JsonProperty("faces")
    private final BBElementFace faces;

    // Mesh fields
    @JsonProperty("vertices")
    private final Map<String, float[]> vertices;
    @JsonProperty("shading")
    private final String shading;

    // Locator / Camera fields
    @JsonProperty("position")
    private final float[] position;
    @JsonProperty("fov")
    private final float fov;

    // TextureMesh fields
    @JsonProperty("local_pivot")
    private final float[] localPivot;
    @JsonProperty("scale")
    private final float[] tmScale;
    @JsonProperty("texture_size")
    private final float[] textureSize;

    // Mesh faces are deserialized separately due to complex structure
    private Map<String, BBMeshFace> meshFaces;

    public BBElement(
            @JsonProperty("uuid") UUID uniqueId,
            @JsonProperty("type") String type,
            @JsonProperty("name") String name,
            @JsonProperty("origin") float[] origin,
            @JsonProperty("rotation") float[] rotation,
            @JsonProperty("box_uv") boolean boxUv,
            @JsonProperty("mirror_uv") boolean mirrorUv,
            @JsonProperty("inflate") double inflate,
            @JsonProperty("from") float[] from,
            @JsonProperty("to") float[] to,
            @JsonProperty("uv_offset") float[] uvOffset,
            @JsonProperty("faces") BBElementFace faces,
            @JsonProperty("vertices") Map<String, float[]> vertices,
            @JsonProperty("shading") String shading,
            @JsonProperty("position") float[] position,
            @JsonProperty("fov") float fov,
            @JsonProperty("local_pivot") float[] localPivot,
            @JsonProperty("scale") float[] tmScale,
            @JsonProperty("texture_size") float[] textureSize) {
        this.uniqueId = uniqueId;
        this.type = type;
        this.name = name;
        this.origin = origin;
        this.rotation = rotation;
        this.boxUv = boxUv;
        this.mirrorUv = mirrorUv;
        this.inflate = inflate;
        this.from = from;
        this.to = to;
        this.uvOffset = uvOffset;
        this.faces = faces;
        this.vertices = vertices;
        this.shading = shading;
        this.position = position;
        this.fov = fov;
        this.localPivot = localPivot;
        this.tmScale = tmScale;
        this.textureSize = textureSize;
    }

    public UUID uniqueId() { return uniqueId; }
    public String type() { return type; }
    public String name() { return name; }
    public float[] origin() { return origin; }
    public float[] rotation() { return rotation; }
    public boolean boxUv() { return boxUv; }
    public boolean mirrorUv() { return mirrorUv; }
    public double inflate() { return inflate; }
    public float[] from() { return from; }
    public float[] to() { return to; }
    public float[] uvOffset() { return uvOffset; }
    public BBElementFace faces() { return faces; }
    public Map<String, float[]> vertices() { return vertices; }
    public String shading() { return shading; }
    public float[] position() { return position; }
    public float fov() { return fov; }
    public float[] localPivot() { return localPivot; }
    public float[] tmScale() { return tmScale; }
    public float[] textureSize() { return textureSize; }

    public Map<String, BBMeshFace> meshFaces() { return meshFaces; }
    public void setMeshFaces(Map<String, BBMeshFace> meshFaces) { this.meshFaces = meshFaces; }

    public boolean isCube() { return type == null || "cube".equalsIgnoreCase(type); }
    public boolean isMesh() { return "mesh".equalsIgnoreCase(type); }
    public boolean isLocator() { return "locator".equalsIgnoreCase(type); }
    public boolean isCamera() { return "camera".equalsIgnoreCase(type); }
    public boolean isTextureMesh() { return "texture_mesh".equalsIgnoreCase(type); }
    public boolean isNullObject() { return "null_object".equalsIgnoreCase(type); }

    public Map<String, BBUv> cubeFacesMap() {
        if (faces == null) return Collections.emptyMap();
        return faces.toMap();
    }
}
