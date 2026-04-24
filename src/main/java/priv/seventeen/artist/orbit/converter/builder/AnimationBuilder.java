package priv.seventeen.artist.orbit.converter.builder;

import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.JsonNodeFactory;
import com.fasterxml.jackson.databind.node.ObjectNode;
import priv.seventeen.artist.orbit.calculator.BezierConverter;
import priv.seventeen.artist.orbit.loader.component.*;

import java.util.*;

public class AnimationBuilder {

    private static final String ROTATION = "rotation";
    private static final String POSITION = "position";
    private static final String SCALE = "scale";
    private static final String FOV = "fov";
    private static final JsonNodeFactory F = JsonNodeFactory.instance;

    private static final ThreadLocal<Boolean> isV5Format = ThreadLocal.withInitial(() -> false);

    private AnimationBuilder() {}

    public static String convertAnimation(BBModel model) {
        if (model.animations() == null || model.animations().isEmpty()) return null;

        boolean v5 = model.isV5Format();
        isV5Format.set(v5);

        try {
            ObjectNode animations = F.objectNode();
            for (BBAnimation animation : model.animations()) {
                if (animation.name() != null) {
                    animations.set(animation.name(), buildAnimation(animation));
                }
            }
            ObjectNode root = F.objectNode();
            root.put("format_version", "1.8.0");
            root.set("animations", animations);
            return root.toString();
        } finally {
            isV5Format.remove();
        }
    }

    private static ObjectNode buildAnimation(BBAnimation animation) {
        ObjectNode json = F.objectNode();
        ObjectNode bonesJson = F.objectNode();
        ObjectNode cameraJson = F.objectNode();
        ObjectNode soundJson = F.objectNode();
        ObjectNode particleJson = F.objectNode();
        ObjectNode timelineJson = F.objectNode();

        setLoopType(json, animation.loop());
        json.put("animation_length", animation.length());

        if (animation.animators() != null) {
            for (BBAnimator animator : animation.animators().values()) {
                if ("effect".equalsIgnoreCase(animator.type()) && animator.keyFrames() != null) {
                    processEffectFrames(animator, soundJson, particleJson, timelineJson);
                } else if (animator.name() != null) {
                    if ("camera".equals(animator.type())) {
                        ObjectNode data = processCameraFrames(animator);
                        if (data != null && !data.isEmpty()) cameraJson.set(animator.name(), data);
                    } else {
                        ObjectNode data = processAnimator(animator);
                        if (data != null && !data.isEmpty()) bonesJson.set(animator.name(), data);
                    }
                }
            }
        }

        json.set("bones", bonesJson);
        json.set("camera", cameraJson);
        json.set("sound_effects", soundJson);
        json.set("particle_effects", particleJson);
        json.set("timeline", timelineJson);
        return json;
    }

    private static void setLoopType(ObjectNode json, String loopType) {
        if ("loop".equals(loopType)) json.put("loop", true);
        else if ("hold".equals(loopType)) json.put("loop", "hold_on_last_frame");
        else json.put("loop", false);
    }

    private static ObjectNode processAnimator(BBAnimator animator) {
        ObjectNode bone = F.objectNode();
        Map<String, List<BBKeyFrame>> channels = sortByChannel(animator);
        processChannel(bone, channels.get(ROTATION), ROTATION);
        processChannel(bone, channels.get(POSITION), POSITION);
        processChannel(bone, channels.get(SCALE), SCALE);
        return bone;
    }

    private static ObjectNode processCameraFrames(BBAnimator animator) {
        ObjectNode camera = F.objectNode();
        Map<String, List<BBKeyFrame>> channels = sortByChannel(animator);
        processChannel(camera, channels.get(ROTATION), ROTATION);
        processChannel(camera, channels.get(POSITION), POSITION);
        processChannel(camera, channels.get(FOV), FOV);
        return camera;
    }

    private static void processEffectFrames(BBAnimator animator, ObjectNode sound, ObjectNode particle, ObjectNode timeline) {
        for (BBKeyFrame frame : animator.keyFrames()) {
            if (frame.dataPoints() == null || frame.dataPoints().isEmpty()) continue;
            String timeKey = String.valueOf(frame.time());

            if ("timeline".equalsIgnoreCase(frame.channel())) {
                String script = frame.dataPoints().get(0).script();
                if (script != null && !script.isEmpty()) timeline.put(timeKey, script);
            } else {
                ArrayNode arr = F.arrayNode();
                for (DataPoint dp : frame.dataPoints()) {
                    ObjectNode eff = F.objectNode();
                    eff.put("effect", dp.effect());
                    if ("particle".equalsIgnoreCase(frame.channel()) && dp.locator() != null && !dp.locator().isEmpty()) {
                        eff.put("locator", dp.locator());
                    }
                    arr.add(eff);
                }
                if ("sound".equalsIgnoreCase(frame.channel())) sound.set(timeKey, arr);
                else if ("particle".equalsIgnoreCase(frame.channel())) particle.set(timeKey, arr);
            }
        }
    }

    private static Map<String, List<BBKeyFrame>> sortByChannel(BBAnimator animator) {
        Map<String, List<BBKeyFrame>> map = new HashMap<>();
        map.put(ROTATION, new ArrayList<>());
        map.put(POSITION, new ArrayList<>());
        map.put(SCALE, new ArrayList<>());
        map.put(FOV, new ArrayList<>());

        if (animator.keyFrames() != null) {
            for (BBKeyFrame kf : animator.keyFrames()) {
                List<BBKeyFrame> list = map.get(kf.channel());
                if (list != null) list.add(kf);
            }
        }
        map.values().forEach(l -> l.sort(Comparator.comparing(BBKeyFrame::time)));
        return map;
    }

    private static void processChannel(ObjectNode bone, List<BBKeyFrame> frames, String channel) {
        if (frames == null || frames.isEmpty()) return;

        int fixType = 0;
        if (isV5Format.get()) {
            if (ROTATION.equals(channel)) fixType = 1;
            else if (POSITION.equals(channel)) fixType = 2;
        }

        if (frames.size() == 1 && Float.compare(frames.get(0).time(), 0.0f) == 0) {
            BBKeyFrame only = frames.get(0);
            if (only.dataPoints() != null && !only.dataPoints().isEmpty()) {
                bone.set(channel, vec3(only.dataPoints().get(0), fixType));
            }
            return;
        }

        ObjectNode channelObj = F.objectNode();
        for (int i = 0; i < frames.size(); i++) {
            BBKeyFrame cur = frames.get(i);
            BBKeyFrame prev = i > 0 ? frames.get(i - 1) : null;
            BBKeyFrame next = i < frames.size() - 1 ? frames.get(i + 1) : null;
            processKeyFrame(channelObj, cur, prev, next, fixType);
        }
        if (!channelObj.isEmpty()) bone.set(channel, channelObj);
    }

    private static void processKeyFrame(ObjectNode target, BBKeyFrame cur, BBKeyFrame prev, BBKeyFrame next, int fix) {
        String interp = cur.interpolation();
        if (interp == null) interp = "linear";
        switch (interp) {
            case "step": handleStep(target, cur, prev, fix); break;
            case "catmullrom": handleCatmullrom(target, cur, prev, fix); break;
            case "bezier": handleBezier(target, cur, prev, next, fix); break;
            default: handleLinear(target, cur, prev, fix); break;
        }
    }

    private static void handleLinear(ObjectNode target, BBKeyFrame frame, BBKeyFrame prev, int fix) {
        String key = String.valueOf(frame.time());
        List<DataPoint> dp = frame.dataPoints();
        if (dp == null) return;

        boolean prevStep = prev != null && "step".equals(prev.interpolation()) && prev.dataPoints() != null && !prev.dataPoints().isEmpty();

        if (dp.size() == 1) {
            if (prevStep) {
                ObjectNode kf = F.objectNode();
                kf.set("pre", vec3(prev.dataPoints().get(prev.dataPoints().size() - 1), fix));
                kf.set("post", vec3(dp.get(0), fix));
                target.set(key, kf);
            } else {
                target.set(key, vec3(dp.get(0), fix));
            }
        } else if (dp.size() >= 2) {
            ObjectNode kf = F.objectNode();
            kf.set("pre", vec3(dp.get(0), fix));
            kf.set("post", vec3(dp.get(1), fix));
            target.set(key, kf);
        }
    }

    private static void handleStep(ObjectNode target, BBKeyFrame frame, BBKeyFrame prev, int fix) {
        String key = String.valueOf(frame.time());
        List<DataPoint> dp = frame.dataPoints();
        if (dp == null || dp.isEmpty()) return;

        boolean prevStep = prev != null && "step".equals(prev.interpolation()) && prev.dataPoints() != null && !prev.dataPoints().isEmpty();

        if (dp.size() == 1) {
            if (prevStep) {
                ObjectNode kf = F.objectNode();
                kf.set("pre", vec3(prev.dataPoints().get(prev.dataPoints().size() - 1), fix));
                kf.set("post", vec3(dp.get(0), fix));
                target.set(key, kf);
            } else {
                target.set(key, vec3(dp.get(0), fix));
            }
        } else {
            ObjectNode kf = F.objectNode();
            kf.set("pre", vec3(dp.get(0), fix));
            kf.set("post", vec3(dp.get(1), fix));
            target.set(key, kf);
        }
    }

    private static void handleCatmullrom(ObjectNode target, BBKeyFrame frame, BBKeyFrame prev, int fix) {
        List<DataPoint> dp = frame.dataPoints();
        if (dp == null || dp.isEmpty()) return;

        boolean prevStep = prev != null && "step".equals(prev.interpolation()) && prev.dataPoints() != null && !prev.dataPoints().isEmpty();

        ObjectNode kf = F.objectNode();
        if (prevStep) kf.set("pre", vec3(prev.dataPoints().get(prev.dataPoints().size() - 1), fix));
        kf.set("post", vec3(dp.get(0), fix));
        kf.put("lerp_mode", "catmullrom");
        target.set(String.valueOf(frame.time()), kf);
    }

    private static void handleBezier(ObjectNode target, BBKeyFrame cur, BBKeyFrame prev, BBKeyFrame next, int fix) {
        List<DataPoint> dp = cur.dataPoints();
        if (dp == null || dp.isEmpty()) return;

        boolean prevStep = prev != null && "step".equals(prev.interpolation()) && prev.dataPoints() != null && !prev.dataPoints().isEmpty();

        if (next != null && next.dataPoints() != null && !next.dataPoints().isEmpty()) {
            float xs = dp.get(0).xAsFloat(), ys = dp.get(0).yAsFloat(), zs = dp.get(0).zAsFloat();
            float xe = next.dataPoints().get(0).xAsFloat(), ye = next.dataPoints().get(0).yAsFloat(), ze = next.dataPoints().get(0).zAsFloat();

            if (fix == 1) { xs = -xs; ys = -ys; xe = -xe; ye = -ye; }
            else if (fix == 2) { xs = -xs; xe = -xe; }

            float[] start = {xs, ys, zs};
            float[] end = {xe, ye, ze};

            Map<String, float[]> samples = BezierConverter.convertBezierKeyframe(
                    cur.time(), next.time(), start, end,
                    cur.bezierRightTime() != null ? cur.bezierRightTime() : new float[]{0, 0, 0},
                    cur.bezierRightValue() != null ? cur.bezierRightValue() : new float[]{0, 0, 0},
                    next.bezierLeftTime() != null ? next.bezierLeftTime() : new float[]{0, 0, 0},
                    next.bezierLeftValue() != null ? next.bezierLeftValue() : new float[]{0, 0, 0}
            );

            String curTimeStr = String.format("%.4f", cur.time());
            for (Map.Entry<String, float[]> entry : samples.entrySet()) {
                ArrayNode val = F.arrayNode().add(entry.getValue()[0]).add(entry.getValue()[1]).add(entry.getValue()[2]);
                if (prevStep && entry.getKey().equals(curTimeStr)) {
                    ObjectNode kf = F.objectNode();
                    kf.set("pre", vec3(prev.dataPoints().get(prev.dataPoints().size() - 1), fix));
                    kf.set("post", val);
                    target.set(entry.getKey(), kf);
                } else {
                    target.set(entry.getKey(), val);
                }
            }
        } else {
            target.set(String.valueOf(cur.time()), vec3(dp.get(0), fix));
        }
    }

    private static ArrayNode vec3(DataPoint point, int fix) {
        if (fix == 1) {
            return F.arrayNode().add(negate(point.xAsString())).add(negate(point.yAsString())).add(point.zAsString());
        } else if (fix == 2) {
            return F.arrayNode().add(negate(point.xAsString())).add(point.yAsString()).add(point.zAsString());
        }
        return F.arrayNode().add(point.xAsString()).add(point.yAsString()).add(point.zAsString());
    }

    private static String negate(String value) {
        if (value == null || value.isEmpty() || "0".equals(value)) return value;
        try {
            double d = Double.parseDouble(value);
            if (d == 0) return "0";
            return value.startsWith("-") ? value.substring(1) : "-" + value;
        } catch (NumberFormatException e) {
            return negateMolang(value);
        }
    }

    private static String negateMolang(String expr) {
        List<String> terms = new ArrayList<>();
        int depth = 0, start = 0;
        for (int i = 0; i < expr.length(); i++) {
            char c = expr.charAt(i);
            if (c == '(') depth++;
            else if (c == ')') depth--;
            else if (depth == 0 && (c == '+' || c == '-') && i > 0) {
                char p = expr.charAt(i - 1);
                if (p != 'e' && p != 'E' && p != '*' && p != '/') {
                    terms.add(expr.substring(start, i));
                    start = i;
                }
            }
        }
        terms.add(expr.substring(start));

        StringBuilder sb = new StringBuilder();
        for (String term : terms) {
            String t = term.trim();
            if (t.isEmpty()) continue;
            if (t.startsWith("-")) {
                sb.append(sb.length() == 0 ? t.substring(1) : "+" + t.substring(1));
            } else if (t.startsWith("+")) {
                sb.append("-").append(t.substring(1));
            } else {
                sb.append("-").append(t);
            }
        }
        return sb.toString();
    }
}
