import com.seedfinding.mcfeature.misc.SlimeChunk;
import com.seedfinding.mcfeature.structure.Mineshaft;
import com.seedfinding.minemap.init.Configs;
import com.seedfinding.minemap.init.Features;
import com.seedfinding.minemap.init.Icons;
import com.seedfinding.minemap.util.data.DrawInfo;
import com.seedfinding.minemap.ui.map.MapContext;
import com.seedfinding.minemap.ui.map.MapSettings;
import com.seedfinding.minemap.ui.map.fragment.Fragment;
import com.seedfinding.mccore.state.Dimension;
import com.seedfinding.mccore.version.MCVersion;

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
