package priv.seventeen.artist.orbit.loader.component;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Map;

public class BBMeshFace {
    private final List<String> vertices;
    private final Map<String, float[]> uv;
    private final int texture;

    @JsonCreator
    public BBMeshFace(
            @JsonProperty("vertices") List<String> vertices,
            @JsonProperty("uv") Map<String, float[]> uv,
            @JsonProperty("texture") int texture) {
        this.vertices = vertices;
        this.uv = uv;
        this.texture = texture;
    }

    public List<String> vertices() { return vertices; }
    public Map<String, float[]> uv() { return uv; }
    public int texture() { return texture; }

    public boolean isTriangle() { return vertices != null && vertices.size() == 3; }
    public boolean isQuad() { return vertices != null && vertices.size() == 4; }
}
