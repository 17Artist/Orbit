package priv.seventeen.artist.orbit.result;

public class Model {
    private final String modelSrc;
    private final byte[] mainTextureSrc;
    private final String mainTextureMetaSrc;
    private final byte[] glowTextureSrc;
    private final String glowTextureMetaSrc;
    private final boolean playerSkin;

    public Model(String modelSrc, byte[] mainTextureSrc, String mainTextureMetaSrc,
                 byte[] glowTextureSrc, String glowTextureMetaSrc, boolean playerSkin) {
        this.modelSrc = modelSrc;
        this.mainTextureSrc = mainTextureSrc;
        this.mainTextureMetaSrc = mainTextureMetaSrc;
        this.glowTextureSrc = glowTextureSrc;
        this.glowTextureMetaSrc = glowTextureMetaSrc;
        this.playerSkin = playerSkin;
    }

    public String modelSrc() { return modelSrc; }
    public byte[] mainTextureSrc() { return mainTextureSrc; }
    public String mainTextureMetaSrc() { return mainTextureMetaSrc; }
    public byte[] glowTextureSrc() { return glowTextureSrc; }
    public String glowTextureMetaSrc() { return glowTextureMetaSrc; }
    public boolean isPlayerSkin() { return playerSkin; }
}
