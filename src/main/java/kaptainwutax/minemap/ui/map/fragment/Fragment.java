package kaptainwutax.minemap.ui.map.fragment;

import jdk.internal.util.Preconditions;
import kaptainwutax.biomeutils.Biome;
import kaptainwutax.biomeutils.layer.BiomeLayer;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.RPos;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.ui.map.IconManager;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.icon.IconRenderer;
import kaptainwutax.minemap.ui.map.tool.Tool;
import kaptainwutax.minemap.util.data.DrawInfo;
import kaptainwutax.minemap.util.math.DisplayMaths;
import kaptainwutax.minemap.util.ui.Graphic;
import org.lwjgl.BufferUtils;
import org.lwjgl.opengl.GL33;
import sun.nio.ch.DirectBuffer;

import java.awt.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.Area;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.nio.ByteBuffer;
import java.util.List;
import java.util.*;

import static org.lwjgl.opengl.GL11.*;
import static org.lwjgl.opengl.GL30.*;

public class Fragment {

    private final int blockX;
    private final int blockZ;
    private final int regionSize;
    private final MapContext context;

    private int layerIdCache;
    private int[][] biomeCache;
    private Set<Biome> activeBiomesCache;
    private ByteBuffer imageCache;

    private Map<Feature<?, ?>, List<BPos>> features;
    private BPos hoveredPos;
    private BPos clickedPos;

    private Integer texture = null;
    private Integer fbo = null;

    public Fragment(int blockX, int blockZ, int regionSize, MapContext context) {
        this.blockX = blockX;
        this.blockZ = blockZ;
        this.regionSize = regionSize;
        this.context = context;
        if (this.context != null) {
            this.refreshBiomeCache();
            this.generateFeatures();
        }
    }

    public Fragment(BPos pos, int regionSize, MapContext context) {
        this(pos.getX(), pos.getZ(), regionSize, context);
    }

    public Fragment(RPos pos, MapContext context) {
        this(pos.toBlockPos(), pos.getRegionSize(), context);
    }
    public boolean isInstantiated(){
        return context != null && biomeCache != null && imageCache != null;
    }
    public boolean isBuilt() {
        return isInstantiated() && texture != null && fbo != null;
    }

    public void build() throws Exception {
        this.refreshBiomeCache();
        if (!isInstantiated()) return;
        if (isBuilt()) return;
        if (!createTexture()) throw new Exception("Failed to create Texture");
        if (!createFBO()) {
            deleteTexture();
            throw new Exception("Failed to create FBO");
        }
        unbindTexture();
        unbindFBO();

    }

    public int getX() {
        return this.blockX;
    }

    public int getZ() {
        return this.blockZ;
    }

    public int getSize() {
        return this.regionSize;
    }

    public MapContext getContext() {
        return this.context;
    }

    public Integer getTexture() {
        return texture;
    }

    public Integer getFbo() {
        return fbo;
    }

    public void drawBiomes(DrawInfo info, int width, int height) {
        if (!isBuilt()) return;

        if (this.imageCache != null && this.texture != null && this.fbo != null && this.context.getSettings().showBiomes) {
            double sX = info.x / (double) width * 2.0 - 1.0;
            double sY = info.y / (double) height * 2.0 - 1.0;
            double iX = info.width / (double) width * 2.0;
            double iY = info.height / (double) height * 2.0;
            glEnable(GL_TEXTURE_2D);
            glBindTexture(GL_TEXTURE_2D, texture);
            glTexEnvi(GL_TEXTURE_ENV, GL_TEXTURE_ENV_MODE, GL_REPLACE);
            glBegin(GL_QUADS);
            glTexCoord2f(0, 0);
            glVertex2d(sX, sY);
            glTexCoord2f(1, 0);
            glVertex2d(sX + iX, sY);
            glTexCoord2f(1, 1);
            glVertex2d(sX + iX, sY + iY);
            glTexCoord2f(0, 1);
            glVertex2d(sX, sY + iY);
            glEnd();
            glDisable(GL_TEXTURE_2D);
//            System.out.println(sX+" "+sY+" "+iX+" "+iY);

            unbindTexture();
        }
    }

    public void drawGrid(Graphics graphics, DrawInfo info) {
        if (this.context==null) return;
        if (this.context.getSettings().showGrid) {
            Color old = graphics.getColor();
            graphics.setColor(Color.BLACK);
            graphics.drawRect(info.x, info.y, info.width, info.height);
            graphics.setColor(old);
        }
    }

    public void drawFeatures(Graphics graphics, DrawInfo info) {
        if (!this.context.getSettings().showFeatures) return;

        Map<Feature<?, ?>, List<BPos>> hovered = this.getHoveredFeatures(info.width, info.height);
        for (Map.Entry<Feature<?, ?>, List<BPos>> entry : this.features.entrySet()) {
            if (!this.context.getSettings().isActive(entry.getKey()) || entry.getValue() == null) continue;

            for (BPos pos : entry.getValue()) {
                this.context.getIconManager().render(graphics, info, entry.getKey(), this, pos, hovered.getOrDefault(entry.getKey(), Collections.emptyList()).contains(pos));
            }
        }
    }

    public void drawTools(Graphics graphics, DrawInfo info, ArrayList<Tool> tools) {
        for (Tool tool : tools) {
            if (tool.isPartial()) {
                Area polygon;
                polygon = new Area(tool.getPartialShape());
                Area rectangle = new Area(this.getRectangle());

                polygon.intersect(rectangle);
                if (!polygon.isEmpty()) {
                    Color old = graphics.getColor();
                    Graphics2D g2d = Graphic.setGoodRendering(Graphic.withoutDithering(graphics));
                    g2d.setColor(tool.getColor());

                    // get the correct polygon in the fragment
                    AffineTransform translateToZero = AffineTransform.getTranslateInstance(-blockX, -blockZ);
                    AffineTransform scaleToDisplayFragment = AffineTransform.getScaleInstance(((double) info.width) / ((double) regionSize), ((double) info.height) / ((double) regionSize));
                    scaleToDisplayFragment.concatenate(translateToZero);
                    AffineTransform translateBackToDisplay = AffineTransform.getTranslateInstance(info.x, info.y);
                    translateBackToDisplay.concatenate(scaleToDisplayFragment);
                    polygon = polygon.createTransformedArea(translateBackToDisplay);

                    // decide to fill or hide artefacts due to fragments
                    if (tool.shouldFill()) {
                        g2d.fill(polygon);
                    }
                    if (tool.shouldHideArtefact()) {
                        Color color = new Color(tool.getColor().getRed(), tool.getColor().getGreen(), tool.getColor().getBlue(), 140);
                        g2d.setColor(color);
                        g2d.fill(polygon);
                    } else {
                        int strokeSize = (int) (((double) regionSize) / info.height);
                        g2d.setStroke(new BasicStroke(DisplayMaths.clamp(strokeSize, 1, 7), BasicStroke.CAP_BUTT, BasicStroke.JOIN_ROUND));
                        g2d.draw(polygon);
                    }
                    g2d.setColor(old);
                }
            }
        }

    }

    public void onHovered(int blockX, int blockZ) {
        this.hoveredPos = new BPos(blockX, 0, blockZ);
    }

    public void onClicked(int blockX, int blockZ) {
        this.clickedPos = new BPos(blockX, 0, blockZ);
    }

    public Map<Feature<?, ?>, List<BPos>> getClickedFeatures(int width, int height) {
        return getFeatures(width, height, this.clickedPos);
    }

    public Map<Feature<?, ?>, List<BPos>> getHoveredFeatures(int width, int height) {
        return getFeatures(width, height, this.hoveredPos);
    }

    public Map<Feature<?, ?>, List<BPos>> getFeatures(int width, int height, BPos checkPos) {
        if (checkPos == null || this.context == null || !this.context.getSettings().showFeatures || this.features == null) {
            return Collections.emptyMap();
        }

        Map<Feature<?, ?>, List<BPos>> map = new HashMap<>();

        for (Map.Entry<Feature<?, ?>, List<BPos>> entry : this.features.entrySet()) {
            if (!this.context.getSettings().isActive(entry.getKey()) || entry.getValue() == null) continue;
            IconRenderer renderer = this.context.getIconManager().getFor(entry.getKey());
            ArrayList<BPos> newList = new ArrayList<>(entry.getValue());
            newList.removeIf(pos -> !renderer.isHovered(this, checkPos, pos, width, height, entry.getKey()));
            map.put(entry.getKey(), newList);
        }

        return map;
    }

    private void refreshBiomeCache() {
        if (this.context==null) return;
        if (this.biomeCache != null && this.layerIdCache == this.context.getLayerId()) return;

        this.layerIdCache = this.context.getLayerId();
        BiomeLayer layer = this.context.getBiomeLayer();
        int effectiveRegion = Math.max(this.regionSize / layer.getScale(), 1);
        RPos region = new BPos(this.blockX, 0, this.blockZ).toRegionPos(layer.getScale());

        if (this.biomeCache == null || this.biomeCache.length != effectiveRegion) {
            this.biomeCache = new int[effectiveRegion][effectiveRegion];
        }

        for (int x = 0; x < effectiveRegion; x++) {
            for (int z = 0; z < effectiveRegion; z++) {
                this.biomeCache[x][z] = layer.get(region.getX() + x, 0, region.getZ() + z);
            }
        }

        this.refreshBufferCache();
    }

    private void refreshBufferCache() {
        if (this.imageCache != null && this.context.getSettings().getActiveBiomes().equals(this.activeBiomesCache)) return;

        int scaledSize = this.biomeCache.length;
        this.activeBiomesCache = this.context.getSettings().getActiveBiomes();
        this.imageCache = BufferUtils.createByteBuffer(scaledSize * scaledSize * 4); // we have to use 4 here (https://www.khronos.org/opengl/wiki/Common_Mistakes#Texture_upload_and_pixel_reads)


        for (int x = 0; x < scaledSize; x++) {
            for (int z = 0; z < scaledSize; z++) {
                Biome biome = Biome.REGISTRY.get(this.biomeCache[z][x]); // warning this need to be inverted for gpu
                if (biome == null) continue;
                Color color = Configs.BIOME_COLORS.get(Configs.USER_PROFILE.getUserSettings().style, biome);

                if (!this.activeBiomesCache.contains(biome)) {
                    color = makeInactive(color);
                }

                this.imageCache.put((byte) color.getRed());
                this.imageCache.put((byte) color.getGreen());
                this.imageCache.put((byte) color.getBlue());
                this.imageCache.put((byte) (0));// no alpha (used to smooth gpu rendering)
            }
        }
        this.imageCache.flip();
    }

    private Color makeInactive(Color c) {
        int r = c.getRed(), g = c.getGreen(), b = c.getBlue(), sum = r + g + b;
        return new Color((r + sum) / 30, (g + sum) / 30, (b + sum) / 30, c.getAlpha());
    }

    private void generateFeatures() {
        this.features = new LinkedHashMap<>();
        IconManager iconManager = this.context.getIconManager();
        for (Feature<?, ?> feature : this.context.getSettings().getAllFeatures(iconManager.getZValueSorter())) {
            List<BPos> positions = iconManager.getPositions(feature, this);
            positions.removeIf(pos -> !this.isPosInFragment(pos));
            this.features.put(feature, positions);
        }
    }

    public boolean isPosInFragment(BPos pos) {
        return this.isPosInFragment(pos.getX(), pos.getZ());
    }

    public boolean isPosInFragment(int blockX, int blockZ) {
        if (blockX < this.getX() || blockX >= this.getX() + this.getSize()) return false;
        return blockZ >= this.getZ() && blockZ < this.getZ() + this.getSize();
    }


    public void unbindTexture() {
        glBindTexture(GL_TEXTURE_2D, 0);
    }

    public void destroy() {
        deleteTexture();
        deleteFBO();
       // ((DirectBuffer) this.imageCache).cleaner().clean(); //TODO fixme
//        this.features.clear();
//        this.activeBiomesCache.clear();
    }

    public void deleteTexture() {
        unbindTexture();
        if (texture != null) {
            glDeleteTextures(texture);
        }
    }

    public void unbindFBO() {
        glBindFramebuffer(GL_FRAMEBUFFER, 0);
    }

    public void deleteFBO() {
        unbindFBO();
        if (fbo != null) {
            glDeleteFramebuffers(fbo);
        }
    }

    public boolean createFBO() {
        if (this.fbo != null) {
            this.deleteFBO();
        }
        if (this.texture == null) {
            return false;
        }
        // create frame buffer
        this.fbo = glGenFramebuffers();
        glBindFramebuffer(GL_FRAMEBUFFER, this.fbo);
        // bind the texture to the fbo
        glFramebufferTexture2D(GL_FRAMEBUFFER, GL_COLOR_ATTACHMENT0, GL_TEXTURE_2D, this.texture, 0);
        return glCheckFramebufferStatus(GL_FRAMEBUFFER) == GL_FRAMEBUFFER_COMPLETE;
    }

    public boolean createTexture() {
        if (this.texture != null) {
            this.deleteTexture();
        }
        if (this.imageCache == null) {
            return false;
        }
        this.texture = glGenTextures();
        glBindTexture(GL_TEXTURE_2D, this.texture);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MIN_FILTER, GL_LINEAR);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_MAG_FILTER, GL_LINEAR);
        // set wrap mode
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_S, GL33.GL_CLAMP_TO_EDGE);
        glTexParameteri(GL_TEXTURE_2D, GL_TEXTURE_WRAP_T, GL33.GL_CLAMP_TO_EDGE);
        return this.refreshTextureCache();
    }

    public boolean refreshTextureCache() {
        if (texture == null) {
            return false;
        }
        if (this.imageCache == null) {
            return false;
        }
        glBindTexture(GL_TEXTURE_2D, this.texture);
        this.imageCache.rewind(); // re reading it
        glTexImage2D(GL_TEXTURE_2D, 0, GL_RGBA8, this.biomeCache.length, this.biomeCache.length, 0, GL_RGBA, GL_UNSIGNED_BYTE, this.imageCache);
        return true;
    }




    public Rectangle getRectangle() {
        return new Rectangle(blockX, blockZ, regionSize, regionSize);
    }

    @Override
    public String toString() {
        return "Fragment{" +
            "blockX=" + blockX +
            ", blockZ=" + blockZ +
            ", regionSize=" + regionSize +
            ", context=" + context +
            ", layerIdCache=" + layerIdCache +
            ", biomeCache=" + Arrays.toString(biomeCache) +
            ", activeBiomesCache=" + activeBiomesCache +
            ", imageCache=" + imageCache +
            ", features=" + features +
            ", hoveredPos=" + hoveredPos +
            '}';
    }
}
