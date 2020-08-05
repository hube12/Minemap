package kaptainwutax.minemap.ui;

import javafx.scene.Cursor;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import kaptainwutax.minemap.util.RegionScheduler;
import kaptainwutax.minemap.world.Fragment;
import kaptainwutax.minemap.world.WorldInfo;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.util.math.Vec3i;

import java.util.HashMap;
import java.util.Map;

public class MapPanel extends Pane {

	public final int blocksPerFragment = 512;
	public double pixelsPerFragment = (int)(300.0D * (this.blocksPerFragment / 512.0D));
	public double centerX;
	public double centerY;
	public Vec3i mousePointer;

	public String tooltip = null;

	public final WorldInfo info;
	public RegionScheduler scheduler;
	private final int threadCount;

	public Canvas canvas;

	public MapPanel(Pane parent, WorldInfo info, int threadCount) {
		this.info = info;
		this.threadCount = threadCount;
		this.canvas = new Canvas(parent.getWidth(), parent.getHeight());

		this.invalidate();

		parent.widthProperty().addListener((observable, oldValue, newValue) -> this.canvas.setWidth(newValue.doubleValue()));
		parent.heightProperty().addListener((observable, oldValue, newValue) -> this.canvas.setHeight(newValue.doubleValue()));

		this.setOnScroll(e -> {
			double newPixelsPerFragment = this.pixelsPerFragment;

			if(e.getDeltaY() > 0) {
				newPixelsPerFragment /= e.getDeltaY() / 60.0D;
			} else {
				newPixelsPerFragment *= -e.getDeltaY() / 60.0D;
			}

			if(newPixelsPerFragment < 40.0D * (this.blocksPerFragment / 512.0D)) {
				newPixelsPerFragment = 40.0D * (this.blocksPerFragment / 512.0D);
			} else if(newPixelsPerFragment > 2000.0D * (this.blocksPerFragment / 512.0D)) {
				newPixelsPerFragment = 2000.0D * (this.blocksPerFragment / 512.0D);
			}

			double scaleFactor = newPixelsPerFragment / this.pixelsPerFragment;
			this.centerX *= scaleFactor;
			this.centerY *= scaleFactor;
			this.pixelsPerFragment = newPixelsPerFragment;
			this.repaint();
		});


		this.setOnMousePressed(e -> {
			this.mousePointer = new Vec3i((int)e.getX(), (int)e.getY(), 0);
			this.getScene().setCursor(Cursor.MOVE);
			this.repaint();
		});

		this.setOnMouseReleased(e -> {
			this.getScene().setCursor(Cursor.DEFAULT);
		});

		this.setOnMouseDragged(e -> {
			int dx = (int)e.getX() - mousePointer.getX();
			int dy = (int)e.getY() - mousePointer.getY();
			mousePointer = new Vec3i((int)e.getX(), (int)e.getY(), 0);
			centerX += dx;
			centerY += dy;
			this.repaint();
		});

		this.setOnMouseMoved(e -> {
			BPos pos = this.getPos(e.getX(), e.getY());
			int x = pos.getX();
			int z = pos.getZ();

			this.tooltip = String.format("Seed %d at (%d, %d): %s", info.worldSeed, x, z, info.getBiome(x, z).getName().toUpperCase());
			this.repaint();
		});

		this.getChildren().add(this.canvas);
	}

	public BPos getPos(double mouseX, double mouseY) {
		double x = (mouseX - this.getWidth() / 2.0D - centerX) / this.getWidth();
		double y = (mouseY - this.getHeight() / 2.0D - centerY) / this.getHeight();
		double blocksPerWidth = (this.getWidth() / pixelsPerFragment) * (double) blocksPerFragment;
		double blocksPerHeight = (this.getHeight() / pixelsPerFragment) * (double) blocksPerFragment;
		x *= blocksPerWidth;
		y *= blocksPerHeight;
		return new BPos((int)Math.round(x), 0, (int)Math.round(y));
	}

	public BPos getCenterPos() {
		return getPos(this.getWidth() / 2.0D, this.getHeight() / 2.0D);
	}

	public void repaint() {
		GraphicsContext g = this.canvas.getGraphicsContext2D();

		g.setFill(Color.BLACK);
		g.fillRect(0, 0, this.getWidth(), this.getHeight());

		this.scheduler.purge();

		double w = this.getWidth();
		double h = this.getHeight();

		Map<Fragment, DrawInfo> drawQueue = new HashMap<>();

		for(int x = -1; x < w / this.pixelsPerFragment + 1; x += Math.max(1, (int)(1.0D / this.pixelsPerFragment))) {
			for(int y = -1; y < h / this.pixelsPerFragment + 1; y += Math.max(1, (int)(1.0D / this.pixelsPerFragment))) {
				Fragment fragment = this.scheduler.getFragmentAt(
						x - (int)((this.centerX + w / 2) / this.pixelsPerFragment),
						y - (int)((this.centerY + h / 2) / this.pixelsPerFragment)
				);

				int x1 = (int)(x * this.pixelsPerFragment + (this.centerX + w / 2) % this.pixelsPerFragment);
				int y1 = (int)(y * this.pixelsPerFragment + (this.centerY + h / 2) % this.pixelsPerFragment);

				if(fragment != null) {
					drawQueue.put(fragment, new DrawInfo(g, x1, y1, (int)this.pixelsPerFragment + 1, (int)this.pixelsPerFragment + 1));
				}
			}
		}

		drawQueue.forEach((fragment, d) -> fragment.drawBiomes(d.g, d.x, d.y, d.width, d.height));
		drawQueue.forEach((fragment, d) -> fragment.drawStructures(d.g, d.x, d.y, d.width, d.height));

		g.setFill(Color.CYAN);
		g.fillOval(this.getWidth() / 2 - 2, this.getHeight() / 2 - 2, 5, 5);

		if(this.tooltip != null) {
			g.setFill(Color.WHITE);
			g.fillText(this.tooltip, 20, 30);
		}
	}

	public void invalidate() {
		this.scheduler = new RegionScheduler(this, threadCount);
		this.repaint();
	}

	public static class DrawInfo {
		public final GraphicsContext g;
		public final double x;
		public final double y;
		public final double width;
		public final double height;

		public DrawInfo(GraphicsContext g, double x, double y, double width, double height) {
			this.g = g;
			this.x = x;
			this.y = y;
			this.width = width;
			this.height = height;
		}
	}

}
