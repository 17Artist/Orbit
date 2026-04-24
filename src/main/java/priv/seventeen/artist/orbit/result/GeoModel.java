package priv.seventeen.artist.orbit.result;

import priv.seventeen.artist.orbit.loader.component.ItemTransform;

import java.util.List;
import java.util.Map;

public class GeoModel {
    private final List<Model> models;
    private final String animationSrc;
    private final Map<String, ItemTransform> transforms;
    private final float width;
    private final float height;
    private final Throwable error;

    public GeoModel(List<Model> models, String animationSrc, Map<String, ItemTransform> transforms,
                    float width, float height, Throwable error) {
        this.models = models;
        this.animationSrc = animationSrc;
        this.transforms = transforms;
        this.width = width;
        this.height = height;
        this.error = error;
    }

    public List<Model> models() { return models; }
    public String animationSrc() { return animationSrc; }
    public Map<String, ItemTransform> transforms() { return transforms; }
    public float width() { return width; }
    public float height() { return height; }
    public Throwable error() { return error; }
    public boolean hasError() { return error != null; }
}
