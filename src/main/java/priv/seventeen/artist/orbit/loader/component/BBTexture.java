package priv.seventeen.artist.orbit.loader.component;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.util.Base64;
import javax.imageio.ImageIO;

@JsonIgnoreProperties(ignoreUnknown = true)
public class BBTexture {
    @JsonProperty("name")
    private final String name;
    @JsonProperty("id")
    private final String id;
    @JsonProperty("width")
    private final int width;
    @JsonProperty("height")
    private final int height;
    @JsonProperty("uv_width")
    private final int uvWidth;
    @JsonProperty("uv_height")
    private final int uvHeight;
    @JsonProperty("source")
    private final String source;
    @JsonProperty("render_mode")
    private final String renderMode;
    @JsonProperty("render_sides")
    private final String renderSides;
    @JsonProperty("fps")
    private final int fps;
    @JsonProperty("frame_time")
    private final int frameTime;
    @JsonProperty("frame_order_type")
    private final String frameOrderType;
    @JsonProperty("frame_order")
    private final String frameOrder;
    @JsonProperty("frame_interpolate")
    private final boolean frameInterpolate;

    private transient BufferedImage image;

    public BBTexture(
            @JsonProperty("name") String name,
            @JsonProperty("id") String id,
            @JsonProperty("width") int width,
            @JsonProperty("height") int height,
            @JsonProperty("uv_width") int uvWidth,
            @JsonProperty("uv_height") int uvHeight,
            @JsonProperty("source") String source,
            @JsonProperty("render_mode") String renderMode,
            @JsonProperty("render_sides") String renderSides,
            @JsonProperty("fps") int fps,
            @JsonProperty("frame_time") int frameTime,
            @JsonProperty("frame_order_type") String frameOrderType,
            @JsonProperty("frame_order") String frameOrder,
            @JsonProperty("frame_interpolate") boolean frameInterpolate) {
        this.name = name;
        this.id = id;
        this.width = width;
        this.height = height;
        this.uvWidth = uvWidth > 0 ? uvWidth : width;
        this.uvHeight = uvHeight > 0 ? uvHeight : height;
        this.source = source;
        this.renderMode = renderMode;
        this.renderSides = renderSides;
        this.fps = fps > 0 ? fps : 7;
        this.frameTime = frameTime > 0 ? frameTime : 1;
        this.frameOrderType = frameOrderType != null ? frameOrderType : "loop";
        this.frameOrder = frameOrder;
        this.frameInterpolate = frameInterpolate;
    }

    public String name() { return name; }
    public String id() { return id; }
    public int width() { return width; }
    public int height() { return height; }
    public int uvWidth() { return uvWidth; }
    public int uvHeight() { return uvHeight; }
    public String source() { return source; }
    public String renderMode() { return renderMode; }
    public String renderSides() { return renderSides; }
    public int fps() { return fps; }
    public int frameTime() { return frameTime; }
    public String frameOrderType() { return frameOrderType; }
    public String frameOrder() { return frameOrder; }
    public boolean frameInterpolate() { return frameInterpolate; }

    public BufferedImage image() {
        if (image == null && source != null) {
            try {
                int commaIndex = source.indexOf(',');
                if (commaIndex != -1) {
                    String encoded = source.substring(commaIndex + 1);
                    byte[] bytes = Base64.getDecoder().decode(encoded);
                    image = ImageIO.read(new ByteArrayInputStream(bytes));
                }
            } catch (Exception ignored) {}
        }
        return image;
    }

    public boolean isAnimated() {
        BufferedImage img = image();
        if (img == null) return false;
        return img.getWidth() < img.getHeight() && img.getHeight() % img.getWidth() == 0;
    }

    public int frameCount() {
        BufferedImage img = image();
        if (img == null || img.getWidth() == 0) return 1;
        if (img.getWidth() < img.getHeight() && img.getHeight() % img.getWidth() == 0) {
            return img.getHeight() / img.getWidth();
        }
        return 1;
    }
}
