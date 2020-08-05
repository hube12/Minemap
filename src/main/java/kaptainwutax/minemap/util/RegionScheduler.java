package kaptainwutax.minemap.util;

import javafx.application.Platform;
import javafx.scene.canvas.GraphicsContext;
import kaptainwutax.minemap.ui.MapPanel;
import kaptainwutax.minemap.world.Fragment;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.RPos;
import kaptainwutax.seedutils.util.math.DistanceMetric;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;

public class RegionScheduler {

	public static Fragment LOADING_FRAGMENT = new Fragment(0, 0, 0, null) {
		@Override
		public void drawBiomes(GraphicsContext g, double x, double y, double width, double height) {
		}

		@Override
		public void drawStructures(GraphicsContext g, double x, double y, double width, double height) {
		}
	};

	protected ThreadPoolExecutor executor;
	protected Map<RPos, Fragment> fragments = new ConcurrentHashMap<>();

	protected MapPanel listener;

	public RegionScheduler(MapPanel listener, int threadCount) {
		this.listener = listener;
		this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(threadCount);
	}

	public void purge() {
		while(this.fragments.size() > 2000) {
			this.removeFarthestFragment();
		}
	}

	private void removeFarthestFragment() {
		RPos farthestFragment = null;
		double farthestDistance = 0.0D;

		BPos center = this.listener.getCenterPos();

		for(Map.Entry<RPos, Fragment> e: this.fragments.entrySet()) {
			RPos fragment = e.getKey();
			double distance = fragment.toBlockPos().distanceTo(center, DistanceMetric.CHEBYSHEV);

			if(distance > farthestDistance) {
				farthestDistance = distance;
				farthestFragment = e.getKey();
			}
		}

		if(farthestFragment != null) {
			this.fragments.remove(farthestFragment);
		}
	}

	public Fragment getFragmentAt(int regionX, int regionZ) {
		int regionSize = this.listener.blocksPerFragment;
		RPos regionPos = new RPos(regionX, regionZ, regionSize);

		if(!this.fragments.containsKey(regionPos)) {
			this.fragments.put(regionPos, LOADING_FRAGMENT);

			executor.execute(() -> {
				try {
					BPos center = this.listener.getCenterPos();

					if(center.distanceTo(regionPos.toBlockPos(), DistanceMetric.CHEBYSHEV) > 14000.0D) {
						this.fragments.remove(regionPos);
						return;
					}

					Fragment fragment = new Fragment(regionX * regionSize, regionZ * regionSize, regionSize, this.listener.info);
					this.fragments.put(regionPos, fragment);
					Platform.runLater(() -> this.listener.repaint());
				} catch(Exception e) {
					e.printStackTrace();
				}
			});
		}

		return this.fragments.get(regionPos);
	}

}
