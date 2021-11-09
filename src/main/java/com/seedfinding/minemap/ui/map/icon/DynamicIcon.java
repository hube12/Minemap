package com.seedfinding.minemap.ui.map.icon;

import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.minemap.init.Icons;
import com.seedfinding.minemap.ui.map.MapContext;
import com.seedfinding.minemap.ui.map.fragment.Fragment;
import com.seedfinding.minemap.util.data.DrawInfo;

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
