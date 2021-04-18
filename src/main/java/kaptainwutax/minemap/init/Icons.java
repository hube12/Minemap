package kaptainwutax.minemap.init;

import kaptainwutax.featureutils.decorator.EndGateway;
import kaptainwutax.featureutils.loot.item.Item;
import kaptainwutax.featureutils.loot.item.Items;
import kaptainwutax.featureutils.misc.SlimeChunk;
import kaptainwutax.featureutils.structure.*;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.version.MCVersion;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.feature.*;
import kaptainwutax.minemap.ui.map.interactive.Chest;
import kaptainwutax.minemap.ui.map.tool.Area;
import kaptainwutax.minemap.ui.map.tool.Circle;
import kaptainwutax.minemap.ui.map.tool.Ruler;
import kaptainwutax.minemap.util.data.Assets;
import kaptainwutax.minemap.util.ui.ModalPopup;
import kaptainwutax.minemap.util.ui.buttons.CloseButton;
import kaptainwutax.minemap.util.ui.buttons.CopyButton;
import kaptainwutax.minemap.util.ui.buttons.InfoButton;
import kaptainwutax.minemap.util.ui.buttons.JumpButton;

import javax.swing.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.FileSystem;
import java.nio.file.FileSystems;
import java.nio.file.Path;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;

import static kaptainwutax.minemap.init.Logger.LOGGER;

public class Icons {

	private static final Map<Class<?>, List<Pair<String, BufferedImage>>> CLASS_REGISTRY = new HashMap<>();
	private static final Map<Object, List<Pair<String, BufferedImage>>> OBJECT_REGISTRY = new HashMap<>();

	public static final Item LEATHER_BOOTS_OVERLAY = new Item("leather_boots_overlay");
	public static final Item LEATHER_LEGGINGS_OVERLAY = new Item("leather_leggings_overlay");
	public static final Item LEATHER_CHESTPLATE_OVERLAY = new Item("leather_chestplate_overlay");
	public static final Item LEATHER_HELMET_OVERLAY = new Item("leather_helmet_overlay");

	public static void registerIcons() {
		String mainPath = "/icon";
		URL url = Icons.class.getResource(mainPath);
		if (url == null) {
			LOGGER.severe(String.format("Url not found for path %s", mainPath));
			return;
		}
		URI uri;
		try {
			uri = url.toURI();
		} catch (URISyntaxException e) {
			LOGGER.severe(String.format("Uri was not able to be converted for url %s with error : %s", url, e.toString()));
			return;
		}
		boolean isJar = "jar".equals(uri.getScheme());
		FileSystem fileSystem = null;
		Path dir;
		if (isJar) {
			try {
				fileSystem = FileSystems.newFileSystem(uri, Collections.emptyMap(), null);
			} catch (IOException e) {
				LOGGER.severe(String.format("Filesystem was not opened correctly for %s with error : %s", uri, e.toString()));
				return;
			}
			dir = fileSystem.getPath(mainPath);
		} else {
			dir = new File(uri).toPath();
		}
		registerJARIcons(dir, isJar);
		registerOwnAssets(dir, isJar);
		if (fileSystem != null) {
			try {
				fileSystem.close();
			} catch (IOException e) {
				LOGGER.severe(String.format("Filesystem was not closed correctly for %s with error : %s", uri, e.toString()));
			}
		}
	}

	public static void registerDelayedIcons(JFrame parent) {
		try {
			boolean r = downloadIcons(parent);
		} catch (Exception e) {
			LOGGER.severe(e.toString());
		}
		registerInternetAssets();
		cleanDuplicates();
	}

	private static boolean downloadIcons(JFrame parent) throws ExecutionException, InterruptedException {
		JDialog downloadPopup = new ModalPopup(parent, "Downloading Assets");
		SwingWorker<Pair<MCVersion, String>, Void> downloadWorker = getDownloadWorker(downloadPopup);
		downloadWorker.execute();
		downloadPopup.setVisible(true);
		Pair<MCVersion, String> clientJarVersion = downloadWorker.get(); // blocking wait (intended)
		downloadPopup.setVisible(false);
		downloadPopup.dispose();
		if (clientJarVersion == null) return false;
		System.out.println("Assets downloaded");
		JDialog extractPopup = new ModalPopup(parent, "Extracting Assets");
		SwingWorker<Boolean, Void> extractWorker = getExtractWorker(clientJarVersion, extractPopup);
		extractWorker.execute();
		extractPopup.setVisible(true);
		Boolean result = extractWorker.get(); // blocking wait (intended)
		extractPopup.setVisible(false);
		extractPopup.dispose();
		Configs.USER_PROFILE.setAssetsVersion(clientJarVersion.getFirst());
		return result;
	}

	private static SwingWorker<Pair<MCVersion, String>, Void> getDownloadWorker(JDialog parent) {
		return new SwingWorker<Pair<MCVersion, String>, Void>() {
			@Override
			protected Pair<MCVersion, String> doInBackground() {
				if (Assets.downloadManifest(Configs.USER_PROFILE.getAssetVersion())) {
					MCVersion version = Assets.getLatestVersion();
					if (version != null) {
						if (Assets.downloadVersionManifest(version, false)) {
							String assetName = Assets.downloadVersionAssets(version, false);
							if (assetName != null) {
								MCVersion assetVersion = MCVersion.fromString(assetName.replace(".json", ""));
								if (assetVersion != null) {
									if (Assets.downloadVersionManifest(assetVersion, false)) {
										String clientName = Assets.downloadClientJar(assetVersion, false);
										if (clientName != null) {
											return new Pair<>(assetVersion, clientName);
										} else {
											Logger.LOGGER.warning("Client jar could not be downloaded");
										}
									} else {
										Logger.LOGGER.warning("Version manifest could not be downloaded");
									}
								} else {
									Logger.LOGGER.warning(String.format("Assets version could not be converted to a viable version for %s", assetName));
								}
							} else {
								Logger.LOGGER.warning("Assets index could not be downloaded");
							}
						} else {
							Logger.LOGGER.warning(String.format("Version manifest could not be downloaded for %s", version));
						}
					} else {
						Logger.LOGGER.warning("Manifest does not contain a valid latest release");
					}
				}
				return null;
			}

			@Override
			protected void done() {
				super.done();
				parent.dispose();
			}
		};
	}

	private static SwingWorker<Boolean, Void> getExtractWorker(Pair<MCVersion, String> result, JDialog parent) {
		return new SwingWorker<Boolean, Void>() {
			@Override
			protected Boolean doInBackground() {
				// it's ok to use / since jarentry aren't platform dependant
				return Assets.extractJar(result.getFirst(), result.getSecond(), jarEntry -> jarEntry.getName().startsWith("assets/minecraft/textures/item") || jarEntry.getName().startsWith("assets/minecraft/textures/block"), false);
			}

			@Override
			protected void done() {
				super.done();
				parent.dispose();
			}
		};
	}

	private static void registerJARIcons(Path dir, boolean isJar) {
		register(BastionRemnant.class, dir, isJar, Structure.getName(BastionRemnant.class));
		register(BuriedTreasure.class, dir, isJar, Structure.getName(BuriedTreasure.class));
		register(DesertPyramid.class, dir, isJar, Structure.getName(DesertPyramid.class));
		register(EndCity.class, dir, isJar, Structure.getName(EndCity.class));
		register(Fortress.class, dir, isJar, Structure.getName(Fortress.class));
		register(Igloo.class, dir, isJar, Structure.getName(Igloo.class));
		register(JunglePyramid.class, dir, isJar, Structure.getName(JunglePyramid.class));
		register(Mansion.class, dir, isJar, Structure.getName(Mansion.class));
		register(Mineshaft.class, dir, isJar, Structure.getName(Mineshaft.class));
		register(Monument.class, dir, isJar, Structure.getName(Monument.class));
		register(OceanRuin.class, dir, isJar, Structure.getName(OceanRuin.class));
		register(PillagerOutpost.class, dir, isJar, Structure.getName(PillagerOutpost.class));
		register(Shipwreck.class, dir, isJar, Structure.getName(Shipwreck.class));
		register(SwampHut.class, dir, isJar, Structure.getName(SwampHut.class));
		register(Village.class, dir, isJar, Structure.getName(Village.class));
		register(Stronghold.class, dir, isJar, Structure.getName(Stronghold.class));

		// special cases
		register(OWRuinedPortal.class, dir, isJar, Structure.getName(RuinedPortal.class));
		register(NERuinedPortal.class, dir, isJar, Structure.getName(RuinedPortal.class));

		register(OWBastionRemnant.class, dir, isJar, Structure.getName(BastionRemnant.class));
		register(OWFortress.class, dir, isJar, Structure.getName(Fortress.class));

		register(NEStronghold.class, dir, isJar, Structure.getName(Stronghold.class));

		// features
		register(Chest.class, dir, isJar, "treasure_chest");
		register(EndGateway.class, dir, isJar, "end_gateway");
		register(SlimeChunk.class, dir, isJar, "slime");
		register(SpawnPoint.class, dir, isJar, "spawn");
		register(NetherFossil.class, dir, isJar, Structure.getName(NetherFossil.class));

		register(Ruler.class, dir, isJar, "ruler");
		register(Area.class, dir, isJar, "area");
		register(Circle.class, dir, isJar, "circle");


		register(CloseButton.class, dir, isJar, "close");
		register(CopyButton.class, dir, isJar, "copy");
		register(JumpButton.class, dir, isJar, "jump");
		register(InfoButton.class, dir, isJar, "info");

		register(MineMap.class, dir, isJar, "logo");

	}

	public static <T> Predicate<T> distinctByKey(Function<? super T, ?> keyExtractor) {
		Set<Object> seen = ConcurrentHashMap.newKeySet();
		return t -> seen.add(keyExtractor.apply(t));
	}

	private static void cleanDuplicates() {
		for (Map.Entry<Class<?>, List<Pair<String, BufferedImage>>> pairs : CLASS_REGISTRY.entrySet()) {
			pairs.setValue(pairs.getValue().stream().filter(distinctByKey(Pair::getFirst)).collect(Collectors.toList()));
		}
		for (Map.Entry<Object, List<Pair<String, BufferedImage>>> pairs : OBJECT_REGISTRY.entrySet()) {
			pairs.setValue(pairs.getValue().stream().filter(distinctByKey(Pair::getFirst)).collect(Collectors.toList()));
		}
	}

	@SuppressWarnings("unused")
	private static void registerOwnAssets(Path dir, boolean isJar) {
//        registerObject(Item.ENCHANTED_GOLDEN_APPLE,dir,isJar,Item.ENCHANTED_GOLDEN_APPLE.getName(),".jpg");
	}

	private static void registerInternetAssets() {
		registerObject(Items.TNT, new File(Assets.DOWNLOAD_DIR_ASSETS).toPath(), false, "tnt_side", ".png");
		registerObject(Items.PUMPKIN, new File(Assets.DOWNLOAD_DIR_ASSETS).toPath(), false, "pumpkin_side", ".png");
		registerObject(Items.ENCHANTED_GOLDEN_APPLE, new File(Assets.DOWNLOAD_DIR_ASSETS).toPath(), false, Items.GOLDEN_APPLE.getName(), ".png");
		registerObject(Items.LIGHT_WEIGHTED_PRESSURE_PLATE, new File(Assets.DOWNLOAD_DIR_ASSETS).toPath(), false, Items.GOLD_BLOCK.getName(), ".png");
		registerObject(Items.CLOCK, new File(Assets.DOWNLOAD_DIR_ASSETS).toPath(), false, "clock_00", ".png");
		registerObject(Items.COMPASS, new File(Assets.DOWNLOAD_DIR_ASSETS).toPath(), false, "compass_00", ".png");
        registerObject(LEATHER_BOOTS_OVERLAY, new File(Assets.DOWNLOAD_DIR_ASSETS).toPath(), false, LEATHER_BOOTS_OVERLAY.getName(), ".png");
        registerObject(LEATHER_LEGGINGS_OVERLAY, new File(Assets.DOWNLOAD_DIR_ASSETS).toPath(), false, LEATHER_LEGGINGS_OVERLAY.getName(), ".png");
        registerObject(LEATHER_CHESTPLATE_OVERLAY, new File(Assets.DOWNLOAD_DIR_ASSETS).toPath(), false, LEATHER_CHESTPLATE_OVERLAY.getName(), ".png");
        registerObject(LEATHER_HELMET_OVERLAY, new File(Assets.DOWNLOAD_DIR_ASSETS).toPath(), false, LEATHER_HELMET_OVERLAY.getName(), ".png");
	}

	@SuppressWarnings("SameParameterValue")
	private static <T> void register(Class<T> clazz, Path dir, boolean isJar, String name, String extension) {
		if (CLASS_REGISTRY.containsKey(clazz)) {
			CLASS_REGISTRY.get(clazz).addAll(Assets.getAsset(dir, isJar, name, extension, path -> path.toAbsolutePath().toString().split("icon")[1]));
		} else {
			CLASS_REGISTRY.put(clazz, Assets.getAsset(dir, isJar, name, extension, path -> path.toAbsolutePath().toString().split("icon")[1]));
		}
	}

	private static <T> void register(Class<T> clazz, Path dir, boolean isJar, String name) {
		register(clazz, dir, isJar, name, ".png");
	}

	@SuppressWarnings("SameParameterValue")
	private static <T> void registerObject(Object object, Path dir, boolean isJar, String name, String extension) {
		if (OBJECT_REGISTRY.containsKey(object)) {
			OBJECT_REGISTRY.get(object).addAll(Assets.getAsset(dir, isJar, name, extension, path -> path.toAbsolutePath().toString().split("assets")[1]));
		} else {
			OBJECT_REGISTRY.put(object, Assets.getAsset(dir, isJar, name, extension, path -> path.toAbsolutePath().toString().split("assets")[1]));
		}
	}

	@SuppressWarnings("unused")
	private static <T> void registerObject(Object object, Path dir, boolean isJar, String name) {
		registerObject(object, dir, isJar, name, ".png");
	}

	public static <T> BufferedImage get(Class<T> clazz) {
		List<Pair<String, BufferedImage>> entry = CLASS_REGISTRY.get(clazz);
		if (entry == null) return null;
		if (entry.isEmpty()) return null;
		// TODO make me config dependant
		return entry.get(entry.size() - 1).getSecond();
	}

	public static BufferedImage getObject(Object object) {
		List<Pair<String, BufferedImage>> entry = OBJECT_REGISTRY.get(object);
		if (entry == null) {
			if (object instanceof Item) {
				registerObject(object, new File(Assets.DOWNLOAD_DIR_ASSETS).toPath(), false, ((Item) object).getName(), ".png");
				List<Pair<String, BufferedImage>> entry2 = OBJECT_REGISTRY.get(object);
				if (entry2 == null) return null;
				entry = entry2;
			} else {
				return null;
			}
		}
		if (entry.isEmpty()) return null;
		// TODO make me config dependant
		return entry.get(0).getSecond();
	}

}
