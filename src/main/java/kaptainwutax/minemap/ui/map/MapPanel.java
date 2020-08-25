package kaptainwutax.minemap.ui.map;

import kaptainwutax.minemap.ui.DrawInfo;
import kaptainwutax.minemap.ui.component.WorldTabs;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.minemap.ui.map.fragment.FragmentScheduler;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.RPos;

import javax.swing.*;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;

public class MapPanel extends JPanel {

	private final MapContext context;
	private final MapManager manager;
	public final MapSideBar displayBar;

	public final int threadCount;
	public FragmentScheduler scheduler;

	public MapPanel(MCVersion version, Dimension dimension, long worldSeed, int threadCount) {
		this.threadCount = threadCount;
		this.setLayout(new BorderLayout());

		this.context = new MapContext(version, dimension, worldSeed);
		this.manager = new MapManager(this);
		this.displayBar = new MapSideBar(this);

		this.setBackground(WorldTabs.BACKGROUND_COLOR.darker().darker());
		this.add(this.displayBar, BorderLayout.WEST);
		this.restart();
	}

	public MapContext getContext() {
		return this.context;
	}

	public MapManager getManager() {
		return this.manager;
	}

	public void restart() {
		if(this.scheduler != null)this.scheduler.terminate();
		this.scheduler = new FragmentScheduler(this, this.threadCount);
		this.repaint();
	}

	@Override
	public void paintComponent(Graphics graphics) {
		super.paintComponent(graphics);
		this.scheduler.purge();
		this.drawMap(graphics);
		this.drawCrossHair(graphics);
	}

	public void drawMap(Graphics graphics) {
		Map<Fragment, DrawInfo> drawQueue = this.getDrawQueue();
		drawQueue.forEach((fragment, info) -> fragment.drawBiomes(graphics, info));
		drawQueue.forEach((fragment, info) -> fragment.drawFeatures(graphics, info));
	}

	public void drawCrossHair(Graphics graphics) {
		graphics.setColor(Color.CYAN);
		graphics.fillOval(this.getWidth() / 2 - 2, this.getHeight() / 2 - 2, 5, 5);
	}

	public Map<Fragment, DrawInfo> getDrawQueue() {
		Map<Fragment, DrawInfo> drawQueue = new HashMap<>();
		int w = this.getWidth(), h = this.getHeight();

		BPos min = this.manager.getPos(0, 0);
		BPos max = this.manager.getPos(w, h);
		RPos regionMin = min.toRegionPos(this.manager.blocksPerFragment);
		RPos regionMax = max.toRegionPos(this.manager.blocksPerFragment);

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
				drawQueue.put(fragment, new DrawInfo((int)x, (int)z, size + 1, size + 1));
			}
		}

		return drawQueue;
	}

	public BufferedImage getScreenshot() {
		BufferedImage image = new BufferedImage(this.getWidth(), this.getHeight(), BufferedImage.TYPE_INT_RGB);
		this.drawMap(image.getGraphics());
		return image;
	}

}
