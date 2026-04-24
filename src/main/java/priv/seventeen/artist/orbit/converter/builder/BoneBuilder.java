package priv.seventeen.artist.orbit.converter.builder;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import priv.seventeen.artist.orbit.converter.wrapper.WrappedOutliner;
import priv.seventeen.artist.orbit.loader.component.BBElement;
import priv.seventeen.artist.orbit.loader.component.BBModel;
import priv.seventeen.artist.orbit.loader.component.BBUv;

import java.util.Map;

public class BoneBuilder {

    private static final JsonNodeFactory F = JsonNodeFactory.instance;

    private BoneBuilder() {}

    public static void recursivelyCreateBones(ArrayNode bones, WrappedOutliner target,
                                               int textureID, BBModel model) {
        bones.add(buildBone(target, textureID, model));
        for (WrappedOutliner child : target.children()) {
            recursivelyCreateBones(bones, child, textureID, model);
        }
    }

    private static ObjectNode buildBone(WrappedOutliner target, int textureID, BBModel model) {
        ObjectNode bone = F.objectNode();
        bone.put("name", target.handler().name());
        if (target.parent() != null) {
            bone.put("parent", target.parent().handler().name());
        }

        float[] origin = target.handler().origin() != null ? target.handler().origin() : new float[]{0, 0, 0};
        bone.set("pivot", vec3(-origin[0], origin[1], origin[2]));

        float[] rotation = target.handler().rotation();
        if (rotation != null) {
            bone.set("rotation", vec3(-rotation[0], -rotation[1], rotation[2]));
        }

        ArrayNode cubes = F.arrayNode();
        ArrayNode meshes = F.arrayNode();
        ObjectNode locators = F.objectNode();
        ObjectNode cameras = F.objectNode();

        int texWidth = model.resolution() != null && model.resolution().width() > 0 ? model.resolution().width() : 16;
        int texHeight = model.resolution() != null && model.resolution().height() > 0 ? model.resolution().height() : 16;

        if (textureID != -999 && model.textures() != null && textureID >= 0 && textureID < model.textures().size()) {
            var tex = model.textures().get(textureID);
            if (tex.uvWidth() > 0) texWidth = tex.uvWidth();
            if (tex.uvHeight() > 0) texHeight = tex.uvHeight();
        }

        for (BBElement element : target.elements()) {
            if (element.isLocator()) {
                locators.set(element.name() != null ? element.name() : "", createLocator(element));
            } else if (element.isCamera()) {
                cameras.set(element.name() != null ? element.name() : "", createCamera(element));
            } else if (element.isMesh() || element.isTextureMesh()) {
                ObjectNode meshNode = MeshBuilder.buildMesh(element, textureID, texWidth, texHeight);
                if (meshNode != null) {
                    meshes.add(meshNode);
                }
            } else if (element.isCube()) {
                ObjectNode cube = createCube(element, textureID, model);
                if (cube != null) {
                    cubes.add(cube);
                }
            }
        }

        bone.set("cubes", cubes);
        if (!meshes.isEmpty()) bone.set("meshes", meshes);
        bone.set("locators", locators);
        bone.set("cameras", cameras);

        return bone;
    }

    private static ObjectNode createLocator(BBElement element) {
        ObjectNode locator = F.objectNode();
        float[] position = element.position();
        if (position != null && position.length == 3) {
            locator.set("offset", vec3(position[0], position[1], position[2]));
        }
        float[] rotation = element.rotation();
        if (rotation != null && rotation.length == 3) {
            locator.set("rotation", vec3(rotation[0], rotation[1], rotation[2]));
        }
        return locator;
    }

    private static ObjectNode createCamera(BBElement element) {
        ObjectNode camera = F.objectNode();
        float[] position = element.position();
        if (position != null && position.length == 3) {
            camera.set("offset", vec3(position[0], position[1], position[2]));
        }
        float[] rotation = element.rotation();
        if (rotation != null && rotation.length == 3) {
            camera.set("rotation", vec3(rotation[0], rotation[1], rotation[2]));
        }
        camera.put("fov", element.fov());
        return camera;
    }

    private static ObjectNode createCube(BBElement element, int textureID, BBModel model) {
        ObjectNode cube = F.objectNode();

        if (element.inflate() != 0.0) {
            cube.put("inflate", element.inflate());
        }

        float[] from = element.from() != null ? element.from() : new float[]{0, 0, 0};
        float[] to = element.to() != null ? element.to() : new float[]{0, 0, 0};

        float sx = to[0] - from[0], sy = to[1] - from[1], sz = to[2] - from[2];
        cube.set("size", vec3(sx, sy, sz));

        boolean isJavaBlock = model.metadata() != null && model.metadata().modelFormat() != null
                && model.metadata().modelFormat().startsWith("java_");

        if (isJavaBlock) {
            float maxX = Math.max(from[0] - 8, to[0] - 8) + (sx < 0 ? sx : 0);
            float minY = Math.min(from[1], to[1]) - (sy < 0 ? sy : 0);
            float minZ = Math.min(from[2] - 8, to[2] - 8) - (sz < 0 ? sz : 0);
            cube.set("origin", vec3(-maxX, minY, minZ));
        } else {
            float maxX = Math.max(from[0], to[0]) + (sx < 0 ? sx : 0);
            float minY = Math.min(from[1], to[1]) - (sy < 0 ? sy : 0);
            float minZ = Math.min(from[2], to[2]) - (sz < 0 ? sz : 0);
            cube.set("origin", vec3(-maxX, minY, minZ));
        }

        float[] cubeOrigin = element.origin() != null ? element.origin() : new float[]{0, 0, 0};
        cube.set("pivot", vec3(-cubeOrigin[0], cubeOrigin[1], cubeOrigin[2]));

        float[] rotation = element.rotation();
        if (rotation != null && rotation.length == 3) {
            cube.set("rotation", vec3(-rotation[0], -rotation[1], rotation[2]));
        }

        return addCubeUVs(cube, element, textureID) ? cube : null;
    }

    private static boolean addCubeUVs(ObjectNode cube, BBElement element, int textureID) {
        if (element.boxUv()) {
            ArrayNode uv = F.arrayNode();
            float[] uvOffset = element.uvOffset();
            if (uvOffset == null || uvOffset.length != 2) {
                uv.add(0).add(0);
            } else {
                uv.add((int) uvOffset[0]).add((int) uvOffset[1]);
            }
            cube.set("uv", uv);
            if (element.mirrorUv()) cube.put("mirror", true);
            return true;
        }

        ObjectNode uv = F.objectNode();
        int faceCount = 0;

        Map<String, BBUv> faces = element.cubeFacesMap();
        for (Map.Entry<String, BBUv> entry : faces.entrySet()) {
            BBUv uvFace = entry.getValue();
            if (uvFace == null || !uvFace.hasTexture()) continue;
            if (textureID != -999 && textureID != uvFace.texture()) continue;

            ObjectNode uvDirection = F.objectNode();
            float[] uvArr = uvFace.uv() != null ? uvFace.uv() : new float[]{0, 0, 0, 0};

            float u = uvArr[0], v = uvArr[1];
            float sizeU = uvArr.length >= 4 ? uvArr[2] - uvArr[0] : 0;
            float sizeV = uvArr.length >= 4 ? uvArr[3] - uvArr[1] : 0;

            String key = entry.getKey();
            if ("up".equals(key) || "down".equals(key)) {
                u += sizeU;
                v += sizeV;
                sizeU *= -1;
                sizeV *= -1;
            }

            if (uvFace.rotation() != 0) {
                uvDirection.put("uv_rotation", uvFace.rotation());
            }

            uvDirection.set("uv", F.arrayNode().add(u).add(v));
            uvDirection.set("uv_size", F.arrayNode().add(sizeU).add(sizeV));
            uv.set(key, uvDirection);
            faceCount++;
        }

        if (faceCount == 0) return false;
        cube.set("uv", uv);
        return true;
    }

    private static ArrayNode vec3(float x, float y, float z) {
        return F.arrayNode().add(x).add(y).add(z);
    }
}
