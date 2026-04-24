package priv.seventeen.artist.orbit.loader;

import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import priv.seventeen.artist.orbit.loader.component.*;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;

public class BBModelReader {

    private final ObjectMapper mapper;

    public BBModelReader() {
        this.mapper = new ObjectMapper();
        this.mapper.configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false);
    }

    public BBModel read(InputStream inputStream) throws IOException {
        JsonNode rootNode = mapper.readTree(inputStream);
        BBModel model = mapper.treeToValue(rootNode, BBModel.class);
        postProcessMeshFaces(rootNode, model);
        postProcessV5Groups(model);
        return model;
    }

    public BBModel read(String json) throws IOException {
        JsonNode rootNode = mapper.readTree(json);
        BBModel model = mapper.treeToValue(rootNode, BBModel.class);
        postProcessMeshFaces(rootNode, model);
        postProcessV5Groups(model);
        return model;
    }

    private void postProcessMeshFaces(JsonNode rootNode, BBModel model) {
        if (model.elements() == null) return;
        JsonNode elementsNode = rootNode.get("elements");
        if (elementsNode == null || !elementsNode.isArray()) return;

        for (int i = 0; i < elementsNode.size() && i < model.elements().size(); i++) {
            BBElement element = model.elements().get(i);
            if (!element.isMesh() && !element.isTextureMesh()) continue;

            JsonNode elementNode = elementsNode.get(i);
            JsonNode facesNode = elementNode.get("faces");
            if (facesNode == null || !facesNode.isObject()) continue;

            Map<String, BBMeshFace> meshFaces = new LinkedHashMap<>();
            Iterator<Map.Entry<String, JsonNode>> fields = facesNode.fields();
            while (fields.hasNext()) {
                Map.Entry<String, JsonNode> entry = fields.next();
                JsonNode faceNode = entry.getValue();

                List<String> vertices = new ArrayList<>();
                JsonNode verticesNode = faceNode.get("vertices");
                if (verticesNode != null && verticesNode.isArray()) {
                    for (JsonNode v : verticesNode) {
                        vertices.add(v.asText());
                    }
                }

                Map<String, float[]> uv = new LinkedHashMap<>();
                JsonNode uvNode = faceNode.get("uv");
                if (uvNode != null && uvNode.isObject()) {
                    Iterator<Map.Entry<String, JsonNode>> uvFields = uvNode.fields();
                    while (uvFields.hasNext()) {
                        Map.Entry<String, JsonNode> uvEntry = uvFields.next();
                        JsonNode uvArray = uvEntry.getValue();
                        if (uvArray.isArray() && uvArray.size() >= 2) {
                            uv.put(uvEntry.getKey(), new float[]{
                                    (float) uvArray.get(0).asDouble(),
                                    (float) uvArray.get(1).asDouble()
                            });
                        }
                    }
                }

                int texture = faceNode.has("texture") ? faceNode.get("texture").asInt(-1) : -1;
                meshFaces.put(entry.getKey(), new BBMeshFace(vertices, uv, texture));
            }
            element.setMeshFaces(meshFaces);
        }
    }

    private void postProcessV5Groups(BBModel model) {
        if (!model.hasGroups() || model.outliner() == null) return;

        Map<UUID, BBGroup> groupMap = new HashMap<>();
        for (var group : model.groups()) {
            groupMap.put(group.uniqueId(), group);
        }

        model.outliner().replaceAll(outliner -> fillOutlinerFromGroups(outliner, groupMap));
    }

    private Outliner fillOutlinerFromGroups(Outliner outliner, Map<UUID, BBGroup> groupMap) {
        // 叶子元素引用（无 name 且无 children）保持为 redirection，用于指向 elements[] 中的 cube/mesh/locator
        if (outliner.isRedirection() && outliner.children().isEmpty()) return outliner;

        var group = outliner.name() == null ? groupMap.get(outliner.uniqueId()) : null;
        var filled = group != null ? outliner.fillFromGroup(group) : outliner;

        filled.children().replaceAll(outliner1 -> fillOutlinerFromGroups(outliner1, groupMap));
        return filled;
    }
}
