package priv.seventeen.artist.orbit.calculator;

import java.util.LinkedHashMap;
import java.util.Map;


public class BezierConverter {

    private static final int SAMPLE_COUNT = 10;
    private static final int NEWTON_ITER = 6;
    private static final float NEWTON_EPS = 1e-4f;

    private BezierConverter() {}

    public static Map<String, float[]> convertBezierKeyframe(
            float startTime, float endTime,
            float[] startValue, float[] endValue,
            float[] startRightTime, float[] startRightValue,
            float[] endLeftTime, float[] endLeftValue) {

        Map<String, float[]> result = new LinkedHashMap<>();
        float duration = endTime - startTime;
        if (duration <= 0) {
            result.put(String.format("%.4f", startTime), startValue.clone());
            return result;
        }

        for (int s = 0; s <= SAMPLE_COUNT; s++) {
            float frac = (float) s / SAMPLE_COUNT;
            float time = startTime + frac * duration;

            float[] value = new float[3];
            for (int axis = 0; axis < 3; axis++) {
                float p0x = startTime;
                float p3x = endTime;
                float p1x = p0x + safeAxis(startRightTime, axis);
                float p2x = p3x + safeAxis(endLeftTime, axis);

                float p0y = startValue[axis];
                float p3y = endValue[axis];
                float p1y = p0y + safeAxis(startRightValue, axis);
                float p2y = p3y + safeAxis(endLeftValue, axis);

                float u = solveU(time, frac, p0x, p1x, p2x, p3x);
                value[axis] = cubic(u, p0y, p1y, p2y, p3y);
            }

            result.put(String.format("%.4f", time), value);
        }

        return result;
    }

    private static float safeAxis(float[] arr, int axis) {
        return arr != null && axis < arr.length ? arr[axis] : 0f;
    }

    /** 牛顿迭代解 cubicBezier(u; p0x,p1x,p2x,p3x) = targetX，初值取 frac。 */
    private static float solveU(float targetX, float initialU,
                                float p0x, float p1x, float p2x, float p3x) {
        float u = initialU;
        for (int i = 0; i < NEWTON_ITER; i++) {
            float x = cubic(u, p0x, p1x, p2x, p3x) - targetX;
            if (Math.abs(x) < NEWTON_EPS) break;
            float dx = cubicDerivative(u, p0x, p1x, p2x, p3x);
            if (Math.abs(dx) < 1e-6f) break;
            u -= x / dx;
            if (u < 0f) u = 0f;
            else if (u > 1f) u = 1f;
        }
        return u;
    }

    private static float cubic(float u, float p0, float p1, float p2, float p3) {
        float v = 1f - u;
        return v * v * v * p0
                + 3f * v * v * u * p1
                + 3f * v * u * u * p2
                + u * u * u * p3;
    }

    private static float cubicDerivative(float u, float p0, float p1, float p2, float p3) {
        float v = 1f - u;
        return 3f * v * v * (p1 - p0)
                + 6f * v * u * (p2 - p1)
                + 3f * u * u * (p3 - p2);
    }
}
