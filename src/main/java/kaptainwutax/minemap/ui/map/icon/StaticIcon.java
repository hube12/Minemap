package kaptainwutax.minemap.ui.map.icon;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.DrawInfo;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.seedutils.mc.pos.BPos;

import java.awt.*;

public abstract class StaticIcon extends IconRenderer {

    public static final int ICON_SIZE = 22;

    public StaticIcon(MapContext context) {
        super(context);
    }

    @Override
    public void render(Graphics graphics, DrawInfo info, Feature<?, ?> feature, Fragment fragment, BPos pos) {
        int sx = (int)((double)(pos.getX() - fragment.getX()) / fragment.getSize() * info.width) - ICON_SIZE / 2;
        int sy = (int)((double)(pos.getZ() - fragment.getZ()) / fragment.getSize() * info.height) - ICON_SIZE / 2;
        graphics.drawImage(Icons.REGISTRY.get(feature.getClass()), info.x + sx, info.y + sy, ICON_SIZE, ICON_SIZE, null);
    }

}
