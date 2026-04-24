package priv.seventeen.artist.orbit.calculator;

import priv.seventeen.artist.orbit.loader.component.BBElement;
import priv.seventeen.artist.orbit.loader.component.BBModel;

import static java.lang.Math.*;

public class VisibleBoundsCalculator {

    private VisibleBoundsCalculator() {}

    public static float[] calculate(BBModel model) {
        float maxX = 0f, minX = 0f, maxY = 0f, minY = 0f, maxZ = 0f, minZ = 0f;

        if (model.elements() != null) {
            for (BBElement element : model.elements()) {
                if (!element.isCube()) continue;
                float[] from = element.from();
                float[] to = element.to();
                if (from == null || to == null || from.length < 3 || to.length < 3) continue;

                maxX = max(maxX, max(from[0], to[0]));
                minX = min(minX, min(from[0], to[0]));
                maxY = max(maxY, max(from[1], to[1]));
                minY = min(minY, min(from[1], to[1]));
                maxZ = max(maxZ, max(from[2], to[2]));
                minZ = min(minZ, min(from[2], to[2]));
            }
        }

        maxX += 8; minX += 8;
        maxY += 8; minY += 8;
        maxZ += 8; minZ += 8;

        float radius = max(max(maxX, maxZ), max(-minX, -minZ));
        if (Float.isInfinite(radius)) radius = 0f;

        float[] visibleBox = model.visibleBox();
        if (visibleBox == null || visibleBox.length < 3) {
            return new float[]{0f, 0f, 0f};
        }

        float width = (float) ceil((radius * 2) / 16f);
        width = max(width, visibleBox[0]);

        float yMin = (float) floor(minY / 16f);
        float yMax = (float) ceil(maxY / 16f);
        if (Float.isInfinite(yMin)) yMin = 0f;
        if (Float.isInfinite(yMax)) yMax = 0f;

        yMin = min(yMin, visibleBox[1] - visibleBox[2] / 2f);
        yMax = max(yMax, visibleBox[1] + visibleBox[2] / 2f);

        return new float[]{
                width,
                yMax - yMin,
                (yMax + yMin) / 2f
        };
    }
}
