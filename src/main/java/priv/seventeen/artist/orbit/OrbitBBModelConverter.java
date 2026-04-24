package priv.seventeen.artist.orbit;

import priv.seventeen.artist.orbit.calculator.VisibleBoundsCalculator;
import priv.seventeen.artist.orbit.converter.builder.AnimationBuilder;
import priv.seventeen.artist.orbit.converter.builder.GeometryBuilder;
import priv.seventeen.artist.orbit.converter.builder.McMetaBuilder;
import priv.seventeen.artist.orbit.converter.wrapper.WrappedModel;
import priv.seventeen.artist.orbit.loader.BBModelReader;
import priv.seventeen.artist.orbit.loader.component.BBModel;
import priv.seventeen.artist.orbit.loader.component.BBTexture;
import priv.seventeen.artist.orbit.result.GeoModel;
import priv.seventeen.artist.orbit.result.Model;

import java.io.InputStream;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

public class OrbitBBModelConverter {

    private final BBModelReader reader;

    public OrbitBBModelConverter() {
        this.reader = new BBModelReader();
    }

    public GeoModel parse(InputStream inputStream) {
        try {
            BBModel bbModel = reader.read(inputStream);
            return convert(bbModel);
        } catch (Exception e) {
            e.printStackTrace();
            return errorModel(e);
        }
    }

    public GeoModel parse(String json) {
        try {
            BBModel bbModel = reader.read(json);
            return convert(bbModel);
        } catch (Exception e) {
            e.printStackTrace();
            return errorModel(e);
        }
    }

    private GeoModel convert(BBModel bbModel) {
        WrappedModel wrapped = new WrappedModel(bbModel);
        String animationSrc = AnimationBuilder.convertAnimation(bbModel);
        List<Model> result = new ArrayList<>();

        List<BBTexture> textures = bbModel.textures();
        if (textures == null || textures.isEmpty()) {
            return errorModel(new Exception("No textures found in model"));
        }

        float[] bounds = VisibleBoundsCalculator.calculate(bbModel);
        boolean singleTexture = bbModel.isSingleTextureFormat();

        if (singleTexture) {
            processSingleTexture(wrapped, textures, bounds, result);
        } else {
            processMultipleTextures(wrapped, textures, bounds, result);
        }

        if (result.isEmpty()) {
            return errorModel(new Exception("Model conversion produced no output"));
        }

        return new GeoModel(result, animationSrc, bbModel.display(), bounds[0], bounds[1], null);
    }

    private void processSingleTexture(WrappedModel wrapped, List<BBTexture> textures, float[] bounds, List<Model> result) {
        BBTexture mainTex = null;
        BBTexture glowTex = null;

        for (BBTexture tex : textures) {
            String name = tex.name();
            if (name == null) continue;
            if (name.endsWith("_glow") || name.endsWith("_glow.png")) {
                glowTex = tex;
            } else if (mainTex == null) {
                mainTex = tex;
            }
        }

        if (mainTex == null) {
            throw new IllegalStateException("No main texture found");
        }

        String modelSrc = GeometryBuilder.buildFromModel(wrapped, -999, bounds);
        addModel(result, modelSrc, mainTex, glowTex);
    }

    private void processMultipleTextures(WrappedModel wrapped, List<BBTexture> textures, float[] bounds, List<Model> result) {
        for (int i = 0; i < textures.size(); i++) {
            BBTexture texture = textures.get(i);
            String name = texture.name();
            if (name == null) continue;
            if (name.endsWith("_glow") || name.endsWith("_glow.png")) continue;

            String modelSrc = GeometryBuilder.buildFromModel(wrapped, i, bounds);
            String baseName = name.endsWith(".png") ? name.substring(0, name.length() - 4) : name;
            BBTexture glowTex = findGlowTexture(textures, baseName);

            addModel(result, modelSrc, texture, glowTex);
        }
    }

    private void addModel(List<Model> result, String modelSrc, BBTexture mainTex, BBTexture glowTex) {
        String textureMeta = McMetaBuilder.createAnimationMeta(mainTex);
        String glowMeta = glowTex != null ? McMetaBuilder.createAnimationMeta(glowTex) : null;

        String texName = mainTex.name();
        boolean isVfx = texName != null && (texName.endsWith("_vfx") || texName.endsWith("_vfx.png"));
        boolean isSkin = texName != null && texName.startsWith("player_skin");

        if (isVfx) {
            result.add(new Model(modelSrc, null, null, decodeBase64(mainTex.source()), textureMeta, false));
        } else if (glowTex != null) {
            result.add(new Model(modelSrc, decodeBase64(mainTex.source()), textureMeta,
                    decodeBase64(glowTex.source()), glowMeta, isSkin));
        } else {
            result.add(new Model(modelSrc, decodeBase64(mainTex.source()), textureMeta, null, null, isSkin));
        }
    }

    private BBTexture findGlowTexture(List<BBTexture> textures, String baseName) {
        return textures.stream()
                .filter(t -> {
                    String n = t.name();
                    return n != null && (n.equals(baseName + "_glow") || n.equals(baseName + "_glow.png"));
                })
                .findFirst().orElse(null);
    }

    private byte[] decodeBase64(String source) {
        if (source == null) return null;
        int comma = source.indexOf(',');
        if (comma == -1) return null;
        try {
            return Base64.getDecoder().decode(source.substring(comma + 1));
        } catch (Exception e) {
            return null;
        }
    }

    private GeoModel errorModel(Exception e) {
        return new GeoModel(new ArrayList<>(), null, null, 0, 0, e);
    }
}
