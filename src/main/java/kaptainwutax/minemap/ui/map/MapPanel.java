package kaptainwutax.minemap.ui.map;

import kaptainwutax.minemap.ui.DrawInfo;
import kaptainwutax.minemap.ui.component.WorldTabs;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.minemap.ui.map.fragment.FragmentScheduler;
import kaptainwutax.minemap.ui.map.tool.LineTool;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.MCVersion;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.RPos;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.geom.Line2D;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class MapPanel extends JPanel {

	public final MapContext context;
	public final MapManager manager;
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
		this.addComponentListener(new ComponentAdapter() {
			public void componentResized(ComponentEvent e) {
				if (e.getComponent().getSize().width<=600){
					if (displayBar.settings.isVisible()){
						displayBar.settings.setVisible(false);
						displayBar.settings.isHiddenForSize=true;
					}
				}else{
					if (displayBar.settings.isHiddenForSize){
						displayBar.settings.setVisible(true);
						displayBar.settings.isHiddenForSize=false;
					}
				}
			}
		});
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
		this.drawLineTool(graphics);
	}

	public void drawMap(Graphics graphics) {
		Map<Fragment, DrawInfo> drawQueue = this.getDrawQueue();
		drawQueue.forEach((fragment, info) -> fragment.drawBiomes(graphics, info));
		drawQueue.forEach((fragment, info) -> fragment.drawFeatures(graphics, info));
	}

	public void drawCrossHair(Graphics graphics) {
		graphics.setXORMode(Color.BLACK);
		int cx = this.getWidth() / 2, cz = this.getHeight() / 2;
		graphics.fillRect(cx - 4, cz - 1, 8, 2);
		graphics.fillRect(cx - 1, cz - 4, 2, 8);
		graphics.setPaintMode();
	}

	public void drawLineTool(Graphics graphics){

		LineTool lineTool=this.manager.lineTool;
		if (lineTool.isLine()){
			Graphics2D g2d = (Graphics2D) graphics;
			g2d.setStroke(new BasicStroke(5));
			g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
			g2d.setRenderingHint(RenderingHints.KEY_ALPHA_INTERPOLATION, RenderingHints.VALUE_ALPHA_INTERPOLATION_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_COLOR_RENDERING, RenderingHints.VALUE_COLOR_RENDER_QUALITY);
			g2d.setRenderingHint(RenderingHints.KEY_DITHERING, RenderingHints.VALUE_DITHER_ENABLE);
			g2d.setColor(Color.RED);
			DrawInfo pos1=lineTool.getPointsDrawing().getFirst();
			DrawInfo pos2=lineTool.getPointsDrawing().getSecond();
			graphics.drawLine(pos1.x,pos1.y,pos2.x,pos2.y);
			g2d.draw(new Line2D.Double(pos1.x,pos1.y,pos2.x,pos2.y));
			g2d.setColor(Color.WHITE);
			g2d.drawString(lineTool.getDistance()+" blocks",pos1.x,pos1.y);
			g2d.setPaintMode();
		}

	}

	public Map<Fragment, DrawInfo> getDrawQueue() {
		Map<Fragment, DrawInfo> drawQueue = new HashMap<>();
		int w = this.getWidth(), h = this.getHeight();

		BPos min = this.manager.getPos(0, 0);
		BPos max = this.manager.getPos(w, h);
		RPos regionMin = min.toRegionPos(this.manager.blocksPerFragment);
		RPos regionMax = max.toRegionPos(this.manager.blocksPerFragment);
		double scaleFactor = this.manager.pixelsPerFragment / this.manager.blocksPerFragment;

		for(int regionX = regionMin.getX(); regionX <= regionMax.getX(); regionX++) {
			for(int regionZ = regionMin.getZ(); regionZ <= regionMax.getZ(); regionZ++) {
				Fragment fragment = this.scheduler.getFragmentAt(regionX, regionZ);
				int blockOffsetX = regionMin.toBlockPos().getX() - min.getX();
				int blockOffsetZ = regionMin.toBlockPos().getZ() - min.getZ();
				double pixelOffsetX = blockOffsetX * scaleFactor;
				double pixelOffsetZ = blockOffsetZ * scaleFactor;
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
