package kaptainwutax.minemap.ui.map.fragment;

import kaptainwutax.minemap.ui.DrawInfo;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.seedutils.mc.pos.BPos;
import kaptainwutax.seedutils.mc.pos.RPos;
import kaptainwutax.seedutils.util.math.DistanceMetric;

import javax.swing.*;
import java.awt.*;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.function.Consumer;

public class FragmentScheduler {

	public static Fragment LOADING_FRAGMENT = new Fragment(0, 0, 0, null) {
		@Override
		public void drawBiomes(Graphics graphics, DrawInfo info) {

		}

		@Override
		public void drawFeatures(Graphics graphics, DrawInfo info) {

		}
	};

	protected ThreadPoolExecutor executor;
	protected Map<RPos, Fragment> fragments = new ConcurrentHashMap<>();

	protected MapPanel listener;

	public FragmentScheduler(MapPanel listener, int threadCount) {
		this.listener = listener;
		this.executor = (ThreadPoolExecutor)Executors.newFixedThreadPool(threadCount);
	}

	public void forEachFragment(Consumer<Fragment> consumer) {
		this.fragments.values().forEach(consumer);
	}

	public void terminate() {
		this.executor.shutdown();
	}

	public void purge() {
		while(this.fragments.size() > 2000) {
			this.removeFarthestFragment();
		}
	}

	private void removeFarthestFragment() {
		RPos farthestFragment = null;
		double farthestDistance = 0.0D;

		BPos center = this.listener.getManager().getCenterPos();

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
		int regionSize = this.listener.getManager().blocksPerFragment;
		RPos regionPos = new RPos(regionX, regionZ, regionSize);

		if(!this.fragments.containsKey(regionPos)) {
			this.fragments.put(regionPos, LOADING_FRAGMENT);

			this.executor.execute(() -> {
				try {
					if(this.executor.isShutdown())return;

					BPos center = this.listener.getManager().getCenterPos();

					if(center.distanceTo(regionPos.toBlockPos(), DistanceMetric.CHEBYSHEV) > 14000.0D) {
						this.fragments.remove(regionPos);
						return;
					}

					Fragment fragment = new Fragment(new RPos(regionX, regionZ, regionSize), this.listener.getContext());
					this.fragments.put(regionPos, fragment);
					SwingUtilities.invokeLater(() -> this.listener.repaint());
				} catch(Exception e) {
					e.printStackTrace();
				}
			});
		}

		return this.fragments.get(regionPos);
	}

}
