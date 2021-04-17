import kaptainwutax.featureutils.misc.SlimeChunk;
import kaptainwutax.featureutils.structure.Mineshaft;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.Features;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.util.data.DrawInfo;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.MapSettings;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.mcutils.state.Dimension;
import kaptainwutax.mcutils.version.MCVersion;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

public class Test {

    public static void main(String[] args) throws IOException {
        //Initializes the icons, features and biome colors.
        Features.registerFeatures();
        Configs.registerConfigs();
        Icons.registerIcons();

        MapSettings settings = new MapSettings(MCVersion.v1_12, Dimension.OVERWORLD).refresh();
        MapContext context = new MapContext(1234L, settings);

        settings.hide(SlimeChunk.class, Mineshaft.class);

        Fragment fragment = new Fragment(-1024, -1024, 2048, context);

        BufferedImage screenshot = getScreenShot(fragment, 2048, 2048);
        ImageIO.write(screenshot, "png", new File(context.worldSeed + ".png"));
    }

    private static BufferedImage getScreenShot(Fragment fragment, int width, int height) {
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        DrawInfo info = new DrawInfo(0, 0, width, height);
        fragment.drawBiomes(image.getGraphics(), info);
        fragment.drawFeatures(image.getGraphics(), info);
        return image;
    }

}
