package priv.seventeen.artist.orbit;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import priv.seventeen.artist.orbit.result.GeoModel;
import priv.seventeen.artist.orbit.result.Model;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class TestParser {

    public static void main(String[] args) throws Exception {
        String inputPath = args.length > 0 ? args[0] : "model.bbmodel";
        String outputDir = args.length > 1 ? args[1] : "output";

        Path outPath = Paths.get(outputDir);
        Files.createDirectories(outPath);

        System.out.println("=== Orbit BBModel Converter v2.0 ===");
        System.out.println("Input: " + inputPath);
        System.out.println("Output: " + outPath.toAbsolutePath());

        OrbitBBModelConverter converter = new OrbitBBModelConverter();
        GeoModel geoModel;

        try (InputStream is = new FileInputStream(inputPath)) {
            geoModel = converter.parse(is);
        }

        if (geoModel.hasError()) {
            System.err.println("ERROR: " + geoModel.error().getMessage());
            geoModel.error().printStackTrace();
            return;
        }

        System.out.println("Models: " + geoModel.models().size());
        System.out.println("Has animation: " + (geoModel.animationSrc() != null));
        System.out.println("Bounds: " + geoModel.width() + " x " + geoModel.height());

        ObjectMapper prettyMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

        for (int i = 0; i < geoModel.models().size(); i++) {
            Model model = geoModel.models().get(i);

            String geoFile = "out" + i + ".geo.json";
            Object geoJson = prettyMapper.readValue(model.modelSrc(), Object.class);
            prettyMapper.writeValue(outPath.resolve(geoFile).toFile(), geoJson);
            System.out.println("  Written: " + geoFile);

            if (model.mainTextureSrc() != null) {
                String texFile = "out" + i + ".png";
                Files.write(outPath.resolve(texFile), model.mainTextureSrc());
                System.out.println("  Written: " + texFile);
            }

            if (model.mainTextureMetaSrc() != null) {
                String metaFile = "out" + i + ".png.mcmeta";
                Object metaJson = prettyMapper.readValue(model.mainTextureMetaSrc(), Object.class);
                prettyMapper.writeValue(outPath.resolve(metaFile).toFile(), metaJson);
                System.out.println("  Written: " + metaFile);
            }

            if (model.glowTextureSrc() != null) {
                String glowFile = "out" + i + "_glow.png";
                Files.write(outPath.resolve(glowFile), model.glowTextureSrc());
                System.out.println("  Written: " + glowFile);
            }

            if (model.glowTextureMetaSrc() != null) {
                String glowMetaFile = "out" + i + "_glow.png.mcmeta";
                Object glowMetaJson = prettyMapper.readValue(model.glowTextureMetaSrc(), Object.class);
                prettyMapper.writeValue(outPath.resolve(glowMetaFile).toFile(), glowMetaJson);
                System.out.println("  Written: " + glowMetaFile);
            }
        }

        if (geoModel.animationSrc() != null) {
            Object animJson = prettyMapper.readValue(geoModel.animationSrc(), Object.class);
            prettyMapper.writeValue(outPath.resolve("out.animation.json").toFile(), animJson);
            System.out.println("  Written: out.animation.json");
        }

        System.out.println("=== Done ===");
    }
}
