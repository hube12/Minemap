package kaptainwutax.minemap.ui.map;

import kaptainwutax.minemap.util.Fragment;
import kaptainwutax.minemap.util.RegionScheduler;
import kaptainwutax.minemap.util.WorldInfo;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.RPos;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MapPanel extends JPanel {

	public final WorldInfo info;
	public final MapManager manager;
	public final MapOptions options;

	public final int threadCount;
	public RegionScheduler scheduler;

	public MapPanel(WorldInfo info, int threadCount) {
		this.info = info;
		this.threadCount = threadCount;

		this.manager = new MapManager(this);
		this.options = new MapOptions(this);

		this.setBackground(Color.BLACK);
		this.add(this.options);

		this.restart();
	}

	public void restart() {
		this.scheduler = new RegionScheduler(this, this.threadCount);
		this.repaint();
	}

	@Override
	public void paintComponent(Graphics g) {
		super.paintComponent(g);

		this.scheduler.purge();

		Map<Fragment, DrawInfo> drawQueue = this.getDrawQueue(g);
		drawQueue.forEach((fragment, d) -> fragment.drawBiomes(d.g, d.x, d.y, d.width, d.height));
		drawQueue.forEach((fragment, d) -> fragment.drawStructures(d.g, d.x, d.y, d.width, d.height));

		g.setColor(Color.CYAN);
		g.fillOval(this.getWidth() / 2 - 2, this.getHeight() / 2 - 2, 5, 5);
	}

	public Map<Fragment, DrawInfo> getDrawQueue(Graphics g) {
		Map<Fragment, DrawInfo> drawQueue = new HashMap<>();
		int w = this.getWidth(), h = this.getHeight();

		BPos min = this.manager.getPos(0, 0);
		BPos max = this.manager.getPos(w, h);
		RPos regionMin = min.toRegionPos(this.manager.blocksPerFragment);
		RPos regionMax = max.toRegionPos(this.manager.blocksPerFragment);
		int scale = this.info.getLayer().getScale();

		for(int regionX = regionMin.getX(); regionX <= regionMax.getX(); regionX++) {
			for(int regionZ = regionMin.getZ(); regionZ <= regionMax.getZ(); regionZ++) {
				Fragment fragment = this.scheduler.getFragmentAt(regionX, regionZ);
				int blockOffsetX = regionMin.toBlockPos().getX() - min.getX();
				int blockOffsetZ = regionMin.toBlockPos().getZ() - min.getZ();
				double pixelOffsetX = blockOffsetX * (this.manager.pixelsPerFragment / this.manager.blocksPerFragment);
				double pixelOffsetZ = blockOffsetZ * (this.manager.pixelsPerFragment / this.manager.blocksPerFragment);
				double x = (regionX - regionMin.getX()) * this.manager.pixelsPerFragment + pixelOffsetX;
				double z = (regionZ - regionMin.getZ()) * this.manager.pixelsPerFragment + pixelOffsetZ;
				int size = (int)(this.manager.pixelsPerFragment);
				drawQueue.put(fragment, new DrawInfo(g, (int)x, (int)z, size + 1, size + 1));
			}
		}

		return drawQueue;
	}

	public BufferedImage getScreenshot() {
		BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		Map<Fragment, DrawInfo> drawQueue = this.getDrawQueue(image.getGraphics());
		drawQueue.forEach((fragment, d) -> fragment.drawBiomes(d.g, d.x, d.y, d.width, d.height));
		drawQueue.forEach((fragment, d) -> fragment.drawStructures(d.g, d.x, d.y, d.width, d.height));
		return image;
	}

}
