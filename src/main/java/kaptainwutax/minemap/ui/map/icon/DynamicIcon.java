package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.minemap.util.data.DrawInfo;
import kaptainwutax.mcutils.util.pos.BPos;

import java.awt.*;

public abstract class DynamicIcon extends IconRenderer {

    private final int blockSize;

    public DynamicIcon(MapContext context, int blockSize) {
        super(context);
        this.blockSize = blockSize;
    }

    @Override
    public void render(Graphics graphics, DrawInfo info, Feature<?, ?> feature, Fragment fragment, BPos pos, boolean hovered) {
        double iconWidth = (double) info.width / fragment.getSize() * this.blockSize;
        double iconHeight = (double) info.height / fragment.getSize() * this.blockSize;

        int sx = (int) ((double) (pos.getX() - fragment.getX()) / fragment.getSize() * info.width);
        int sy = (int) ((double) (pos.getZ() - fragment.getZ()) / fragment.getSize() * info.height);
        graphics.drawImage(Icons.get(feature.getClass()), info.x + sx, info.y + sy, (int) iconWidth + 1, (int) iconHeight + 1, null);
    }

}
