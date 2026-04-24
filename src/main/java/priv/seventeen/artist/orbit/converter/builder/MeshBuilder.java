package priv.seventeen.artist.orbit.converter.builder;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import priv.seventeen.artist.orbit.loader.component.BBElement;
import priv.seventeen.artist.orbit.loader.component.BBMeshFace;

import java.util.*;

public class MeshBuilder {

    private static final JsonNodeFactory F = JsonNodeFactory.instance;

    private MeshBuilder() {}

    /**
     * Converts a mesh BBElement into a JSON mesh node for the geo format.
     * Vertices are unrolled (per-face per-vertex) so that each vertex carries its own UV and normal.
     * Triangles and quads are stored in separate index arrays for mixed-mode rendering.
     *
     * @param element   the mesh element
     * @param textureID texture filter (-999 = accept all)
     * @param texWidth  texture UV width for normalization
     * @param texHeight texture UV height for normalization
     * @return the mesh JSON node, or null if no faces match the texture filter
     */
    public static ObjectNode buildMesh(BBElement element, int textureID, int texWidth, int texHeight) {
        if (element.meshFaces() == null || element.vertices() == null) return null;
        if (texWidth <= 0) texWidth = 16;
        if (texHeight <= 0) texHeight = 16;

        Map<String, float[]> srcVertices = element.vertices();
        Map<String, BBMeshFace> srcFaces = element.meshFaces();

        List<float[]> outPositions = new ArrayList<>();
        List<float[]> outNormals = new ArrayList<>();
        List<float[]> outUvs = new ArrayList<>();
        List<Integer> triIndices = new ArrayList<>();
        List<Integer> quadIndices = new ArrayList<>();

        int faceCount = 0;

        for (Map.Entry<String, BBMeshFace> entry : srcFaces.entrySet()) {
            BBMeshFace face = entry.getValue();
            if (face.vertices() == null || face.vertices().size() < 3) continue;

            if (textureID != -999 && face.texture() != textureID) continue;

            List<String> vkeys = face.vertices();
            int vertCount = vkeys.size();
            if (vertCount < 3 || vertCount > 4) continue;

            float[] normal = calculateFaceNormal(srcVertices, vkeys);
            float nxFlipped = -normal[0];

            int baseIndex = outPositions.size();

            for (String vkey : vkeys) {
                float[] pos = srcVertices.get(vkey);
                if (pos == null) pos = new float[]{0, 0, 0};

                outPositions.add(new float[]{-pos[0], pos[1], pos[2]});
                outNormals.add(new float[]{nxFlipped, normal[1], normal[2]});

                float[] uv = face.uv() != null ? face.uv().get(vkey) : null;
                if (uv != null && uv.length >= 2) {
                    outUvs.add(new float[]{uv[0] / texWidth, uv[1] / texHeight});
                } else {
                    outUvs.add(new float[]{0, 0});
                }
            }

            if (vertCount == 3) {
                triIndices.add(baseIndex);
                triIndices.add(baseIndex + 1);
                triIndices.add(baseIndex + 2);
            } else {
                quadIndices.add(baseIndex);
                quadIndices.add(baseIndex + 1);
                quadIndices.add(baseIndex + 2);
                quadIndices.add(baseIndex + 3);
            }

            faceCount++;
        }

        if (faceCount == 0) return null;

        ObjectNode mesh = F.objectNode();
        mesh.put("name", element.name() != null ? element.name() : "mesh");

        float[] origin = element.origin();
        if (origin != null && origin.length == 3) {
            mesh.set("origin", vec3(-origin[0], origin[1], origin[2]));
        }

        float[] rotation = element.rotation();
        if (rotation != null && rotation.length == 3) {
            mesh.set("rotation", vec3(-rotation[0], -rotation[1], rotation[2]));
        }

        mesh.set("vertices", toFloat2DArray(outPositions));
        mesh.set("normals", toFloat2DArray(outNormals));
        mesh.set("uvs", toFloat2DArray(outUvs));
        mesh.set("triangles", toIntArray(triIndices));
        mesh.set("quads", toIntArray(quadIndices));

        return mesh;
    }

    private static float[] calculateFaceNormal(Map<String, float[]> vertices, List<String> vkeys) {
        float[] v0 = vertices.getOrDefault(vkeys.get(0), new float[]{0, 0, 0});
        float[] v1 = vertices.getOrDefault(vkeys.get(1), new float[]{0, 0, 0});
        float[] v2 = vertices.getOrDefault(vkeys.get(vkeys.size() > 3 ? 3 : 2), new float[]{0, 0, 0});

        float ax = v1[0] - v0[0], ay = v1[1] - v0[1], az = v1[2] - v0[2];
        float bx = v2[0] - v0[0], by = v2[1] - v0[1], bz = v2[2] - v0[2];

        float nx = ay * bz - az * by;
        float ny = az * bx - ax * bz;
        float nz = ax * by - ay * bx;

        float len = (float) Math.sqrt(nx * nx + ny * ny + nz * nz);
        if (len > 0.0001f) {
            nx /= len;
            ny /= len;
            nz /= len;
        }
        return new float[]{nx, ny, nz};
    }

    private static ArrayNode vec3(float x, float y, float z) {
        return F.arrayNode().add(x).add(y).add(z);
    }

    private static ArrayNode toFloat2DArray(List<float[]> list) {
        ArrayNode arr = F.arrayNode();
        for (float[] v : list) {
            ArrayNode inner = F.arrayNode();
            for (float f : v) inner.add(f);
            arr.add(inner);
        }
        return arr;
    }

    private static ArrayNode toIntArray(List<Integer> list) {
        ArrayNode arr = F.arrayNode();
        for (int i : list) arr.add(i);
        return arr;
    }
}
