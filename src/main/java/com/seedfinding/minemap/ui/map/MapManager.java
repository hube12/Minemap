package com.seedfinding.minemap.ui.map;

import com.seedfinding.mccore.util.data.Pair;
import com.seedfinding.mccore.util.math.Vec3i;
import com.seedfinding.mccore.util.pos.BPos;
import com.seedfinding.mcfeature.Feature;
import com.seedfinding.mcfeature.structure.RegionStructure;
import com.seedfinding.mcfeature.structure.RuinedPortal;
import com.seedfinding.mcmath.util.Mth;
import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.init.Configs;
import com.seedfinding.minemap.init.Icons;
import com.seedfinding.minemap.init.Logger;
import com.seedfinding.minemap.listener.Events;
import com.seedfinding.minemap.ui.dialog.RenameTabDialog;
import com.seedfinding.minemap.ui.map.interactive.Portal;
import com.seedfinding.minemap.ui.map.interactive.chest.ChestFrame;
import com.seedfinding.minemap.ui.map.tool.*;
import com.seedfinding.minemap.util.math.DisplayMaths;
import com.seedfinding.minemap.util.misc.FindOnMap;
import com.seedfinding.minemap.util.ui.graphics.Icon;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

public class MapManager {

    public static final int DEFAULT_REGION_SIZE = 512;//8192;
    public static final double DEFAULT_PIXELS_PER_FRAGMENT = 256.0;
    public int blocksPerFragment;
    public final ArrayList<Tool> toolsList = new ArrayList<>();
    private final MapPanel panel;
    public final JPopupMenu popup;
    private final ChestFrame chestWindows;
    private final Portal portalMenu;
    public double pixelsPerFragment;
    public double centerX;
    public double centerY;
    public Tool selectedTool = null;

    public Point mousePointer;

    public MapManager(MapPanel panel) {
        this(panel, DEFAULT_REGION_SIZE);
    }

    public MapManager(MapPanel panel, int blocksPerFragment) {
        this.panel = panel;
        this.blocksPerFragment = blocksPerFragment;
        this.pixelsPerFragment = (int) (DEFAULT_PIXELS_PER_FRAGMENT * (this.blocksPerFragment / DEFAULT_REGION_SIZE));

        this.panel.addMouseMotionListener(Events.Mouse.onDragged(e -> {
            if (this.mousePointer == null) return;
            if (SwingUtilities.isLeftMouseButton(e)) {
                int dx = e.getX() - this.mousePointer.x;
                int dy = e.getY() - this.mousePointer.y;
                this.mousePointer = e.getPoint();
                this.centerX += dx;
                this.centerY += dy;
                this.panel.repaint();
            }
        }));

        this.panel.addMouseMotionListener(Events.Mouse.onMoved(e -> {
            this.mousePointer = e.getPoint();
            BPos pos = this.getMouseBPos();
            int x = pos.getX();
            int z = pos.getZ();
            this.panel.scheduler.forEachFragment(fragment -> fragment.onHovered(x, z));

            SwingUtilities.invokeLater(() -> {
                this.panel.leftBar.tooltip.updateBiomeDisplay(x, z);
                this.panel.leftBar.tooltip.tooltip.repaint();
                this.panel.repaint();
            });

        }));

        // update initial so we don't have a weird black box at the top left corner
        SwingUtilities.invokeLater(updateInit());

        this.panel.addMouseListener(Events.Mouse.onPressed(e -> {
            if (SwingUtilities.isLeftMouseButton(e)) {
                this.mousePointer = e.getPoint();
                BPos pos = this.getMouseBPos();
                this.panel.scheduler.forEachFragment(fragment -> fragment.onClicked(pos.getX(), pos.getZ()));
                if (selectedTool == null) {
                    ArrayList<Pair<Feature<?, ?>, List<BPos>>> features = FindOnMap.findFeaturesSelected();
                    if (features != null && !features.isEmpty()) {
                        for (Pair<Feature<?, ?>, List<BPos>> featureListPair : features) {
                            Feature<?, ?> feature = featureListPair.getFirst();
                            if (feature instanceof RegionStructure<?, ?>) {
                                for (BPos bPos : featureListPair.getSecond()) {
                                    this.generateChest(feature, bPos);
                                }
                            }
                        }
                    } else {
                        this.panel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    }
                } else {
                    // if tool has no more points to it
                    if (!selectedTool.addPoint(pos)) {
                        selectedTool = selectedTool.duplicate();
                        toolsList.add(selectedTool);
                        selectedTool.addPoint(pos);
                    }
                    // weird case when removed
                    if (selectedTool.getPointsTraced() > 0 && toolsList.isEmpty()) {
                        toolsList.add(selectedTool);
                    }
                }
                this.panel.rightBar.tooltip.updateToolsMetrics();
            }
        }));

        this.panel.addMouseListener(Events.Mouse.onReleased(e -> {
            if (SwingUtilities.isLeftMouseButton(e)) {
                if (selectedTool == null) {
                    this.panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                }
            }
        }));

        this.panel.addMouseWheelListener(e -> {
            boolean isModifier = Configs.USER_PROFILE.getUserSettings().modifierDown.getModifier().apply(e);
            boolean zoomIn = e.getUnitsToScroll() > 0;
            zoom(zoomIn, isModifier).run();
        });

        this.popup = new JPopupMenu();

        JMenuItem save = new JMenuItem("Save tab");
        save.setBorder(new EmptyBorder(5, 15, 5, 15));
        if (this.panel.getHeader() != null && this.panel.getHeader().isSaved()) {
            save.setText("Unsave tab");
        }
        save.addMouseListener(Events.Mouse.onReleased(e -> {
            boolean newState = !MineMap.INSTANCE.worldTabs.getSelectedHeader().isSaved();
            MineMap.INSTANCE.worldTabs.getSelectedHeader().setSaved(newState);
            save.setText(newState ? "Unsave Tab" : "Save Tab");
        }));
        popup.add(save);

        JMenuItem rename = new JMenuItem("Rename");
        rename.setBorder(new EmptyBorder(5, 15, 5, 15));

        rename.addMouseListener(Events.Mouse.onReleased(e -> {
            RenameTabDialog renameTabDialog = new RenameTabDialog(() -> {});
            renameTabDialog.setVisible(true);
        }));
        popup.add(rename);

        JMenuItem settings = new JMenuItem("Settings");
        settings.setBorder(new EmptyBorder(5, 15, 5, 15));
        settings.addMouseListener(Events.Mouse.onReleased(e -> this.panel.leftBar.settings.setVisible(!panel.leftBar.settings.isVisible())));

        chestWindows = new ChestFrame(this.panel.chestInstance);
        portalMenu = new Portal(this.panel);

        popup.add(settings);
        List<Supplier<Tool>> tools = Arrays.asList(Ruler::new, Area::new, Circle::new, Polyline::new);
        this.addTools(popup, tools);
        popup.addPopupMenuListener(new PopupMenuListener() {
                                       @Override
                                       public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                                           popup.removeAll();
                                           ArrayList<Pair<Feature<?, ?>, List<BPos>>> features = FindOnMap.findFeaturesSelected();
                                           if (features != null && !features.isEmpty()) {
                                               for (Pair<Feature<?, ?>, List<BPos>> featureListPair : features) {
                                                   processFeaturePositions(featureListPair);
                                               }
                                               BPos centroid = DisplayMaths.getCentroid(features.stream().flatMap(ll -> ll.getSecond().stream()).collect(Collectors.toList()));
                                               JMenuItem copyTp = new JMenuItem("Copy TP");
                                               copyTp.setHorizontalTextPosition(SwingConstants.CENTER);
                                               copyTp.setHorizontalAlignment(SwingConstants.CENTER);
                                               copyTp.addMouseListener(Events.Mouse.onReleased(ecp -> {
                                                       Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                                       String tpCommand = String.format("/tp @p %d ~ %d", centroid.getX(), centroid.getZ());
                                                       StringSelection stringSelection = new StringSelection(tpCommand);
                                                       clipboard.setContents(stringSelection, null);
                                                   }
                                               ));
                                               popup.add(copyTp);
                                           } else {
                                               popup.add(save);
                                               popup.add(rename);
                                               popup.add(settings);
                                               addTools(popup, tools);
                                           }
                                       }

                                       @Override
                                       public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                                       }

                                       @Override
                                       public void popupMenuCanceled(PopupMenuEvent e) {}
                                   }
        );
        this.panel.setComponentPopupMenu(popup);
    }

    public void processFeaturePositions(Pair<Feature<?, ?>, List<BPos>> featureListPair) {
        Feature<?, ?> feature = featureListPair.getFirst();
        // chest are only valid for region structure for now (mineshaft are coming)
        if (feature instanceof RegionStructure<?, ?>) {
            ImageIcon icon = Icon.getIcon(feature.getClass(), 25, 18, Icons.get(ChestFrame.class));
            for (BPos pos : featureListPair.getSecond()) {
                processFeatureChest(feature, pos, icon);
            }
        }
        if (feature instanceof RuinedPortal) {
            ImageIcon icon = Icon.getIcon(feature.getClass(), 25, 22, null);
            for (BPos pos : featureListPair.getSecond()) {
                processFeaturePortal(feature, pos, icon);
            }
        }
    }

    public void generateChest(Feature<?, ?> feature, BPos pos) {
        this.panel.chestInstance.setPos(pos.toChunkPos());
        this.panel.chestInstance.setFeature(feature);
        this.panel.chestInstance.generate(); // this calls all the update function to generate the chests
        this.panel.chestInstance.setCurrentChestIndex(0);
        // however we still have some "top" init to do
        this.chestWindows.updateFirst();
    }

    public void processFeatureChest(Feature<?, ?> feature, BPos pos, ImageIcon icon) {
        JMenuItem chestMenu = new JMenuItem(String.format("Chest (%d,%d)", pos.getX(), pos.getZ()), icon);
        chestMenu.setBorder(new EmptyBorder(5, 15, 5, 15));
        chestMenu.addMouseListener(Events.Mouse.onReleased(me -> {
            this.generateChest(feature, pos);
            this.chestWindows.setVisible(true);
        }));
        popup.add(chestMenu);
        popup.add(new JSeparator());
    }

    public void processFeaturePortal(Feature<?, ?> feature, BPos pos, ImageIcon icon) {
        JMenuItem portal = new JMenuItem(String.format("Portal (%d,%d)", pos.getX(), pos.getZ()), icon);
        portal.setBorder(new EmptyBorder(5, 15, 5, 15));
        portal.addMouseListener(Events.Mouse.onReleased(me -> {
            portalMenu.setPos(pos.toChunkPos());
            portalMenu.setFeature((RuinedPortal) feature);
            if (!portalMenu.generateContent()) {
                System.err.println("Portal not generated for " + pos);
                Logger.LOGGER.severe("Portal not generated for " + pos);
            }
            portalMenu.run();
        }));
        popup.add(portal);
        popup.add(new JSeparator());
    }

    public Runnable updateInit() {
        return () -> SwingUtilities.invokeLater(
            () -> {
                if (this.panel.leftBar == null || this.panel.leftBar.tooltip == null) {
                    SwingUtilities.invokeLater(updateInit());
                } else {
                    this.panel.leftBar.tooltip.updateBiomeDisplay(0, 0);
                }
            }
        );
    }

    public void updateInteractive() {
    }

    @SuppressWarnings("unused")
    public Point getMousePointer() {
        return mousePointer;
    }

    public BPos getMouseBPos() {
        return this.getPos(mousePointer.x, mousePointer.y);
    }

    public static Runnable zoom(boolean zoomOut, boolean isModifier) {
        return () -> {
            if (MineMap.INSTANCE == null) return;
            if (MineMap.INSTANCE.worldTabs == null) return;
            if (MineMap.INSTANCE.worldTabs.getSelectedMapPanel() == null) return;
            if (MineMap.INSTANCE.worldTabs.getSelectedMapPanel().manager == null) return;
            MapManager manager = MineMap.INSTANCE.worldTabs.getSelectedMapPanel().manager;
            if (!isModifier) {
                double newPixelsPerFragment = manager.pixelsPerFragment;

                if (zoomOut) {
                    newPixelsPerFragment /= 2.0D;
                } else {
                    newPixelsPerFragment *= 2.0D;
                }

                if (newPixelsPerFragment > 4096.0D * (double) manager.blocksPerFragment / DEFAULT_REGION_SIZE) {
                    // restrict min zoom to 4096 chunks per fragment
                    newPixelsPerFragment = 4096.0D * (manager.blocksPerFragment / 512.0D);
                } else if (Configs.USER_PROFILE.getUserSettings().restrictMaximumZoom && newPixelsPerFragment < 32.0D * (double) manager.blocksPerFragment / DEFAULT_REGION_SIZE) {
                    // restrict max zoom to 32 chunks per fragment
                    newPixelsPerFragment = 32.0D * (manager.blocksPerFragment / 512.0D);
                }

                double scaleFactor = newPixelsPerFragment / manager.pixelsPerFragment;
                manager.centerX *= scaleFactor;
                manager.centerY *= scaleFactor;
                manager.pixelsPerFragment = newPixelsPerFragment;
                manager.panel.repaint();
            } else {
                int layerId = manager.panel.getContext().getLayerId();
                layerId += zoomOut ? -1 : 1;
                layerId = Mth.clamp(layerId, 0, manager.panel.getContext().getBiomeSource().getLayerCount() - 1);

                if (manager.panel.getContext().getLayerId() != layerId) {
                    manager.panel.getContext().setLayerId(layerId);
                    manager.panel.leftBar.settings.layerDropdown.selectIfPresent(layerId);
                    manager.panel.restart();
                }
            }
        };
    }

    public void addTools(JPopupMenu popup, List<Supplier<Tool>> tools) {
        List<JMenuItem> toolMenus = new ArrayList<>();
        for (int i = 0; i < tools.size(); i++) {
            JMenuItem toolMenu = new JMenuItem();
            toolMenu.setBorder(new EmptyBorder(5, 15, 5, 15));
            toolMenus.add(toolMenu);
        }
        Consumer<Pair<String, String>> rTools = prefix -> {
            for (int i = 0; i < tools.size(); i++) {
                Tool currentTool = tools.get(i).get();
                toolMenus.get(i).setText(String.join(" ",
                    selectedTool != null && selectedTool.getName().equals(currentTool.getName()) ?
                        prefix.getSecond() : prefix.getFirst(), currentTool.getName()));
            }
        };
        rTools.accept(new Pair<>("Enable", "Disable"));

        BiConsumer<Tool, JMenuItem> createNewTool = (newTool, menuItem) -> {
            toolsList.add(newTool);
            selectedTool = newTool;
            this.panel.setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
            menuItem.setText("Disable " + newTool.getName());
        };

        BiConsumer<Supplier<Tool>, JMenuItem> toggleTool = (newTool, menuItem) -> {
            Tool tool = newTool.get(); // to avoid creating an instance at one point
            if (selectedTool == null) {
                createNewTool.accept(tool, menuItem);
            } else {
                this.panel.setCursor(Cursor.getPredefinedCursor(Cursor.DEFAULT_CURSOR));
                rTools.accept(new Pair<>("Enable", "Disable"));
                if (!selectedTool.isAcceptable()) {
                    removeTool(selectedTool);
                    this.panel.rightBar.tooltip.updateToolsMetrics();
                }
                if (tool.getClass().equals(selectedTool.getClass())) {
                    selectedTool = null;
                } else {
                    createNewTool.accept(tool, menuItem);
                }
            }

        };

        for (int i = 0; i < tools.size(); i++) {
            JMenuItem toolMenu = toolMenus.get(i);
            Supplier<Tool> tool = tools.get(i);
            toolMenu.addMouseListener(Events.Mouse.onReleased(e -> toggleTool.accept(tool, toolMenu)));
            popup.add(toolMenu);
        }
    }

    public void removeTool(Tool tool) {
        if (selectedTool == tool) {
            selectedTool = tool.duplicate();
            selectedTool.reset();
        }
        if (!toolsList.remove(tool)) {
            System.out.println("This is unexpected");
        }
    }

    public Vec3i getScreenSize() {
        return new Vec3i(this.panel.getWidth(), 0, this.panel.getHeight());
    }

    public BPos getCenterPos() {
        Vec3i screenSize = this.getScreenSize();
        return getPos(screenSize.getX() / 2.0D, screenSize.getZ() / 2.0D);
    }

    public void setCenterPos(int blockX, int blockZ) {
        double scaleFactor = this.pixelsPerFragment / this.blocksPerFragment;
        this.centerX = -blockX * scaleFactor;
        this.centerY = -blockZ * scaleFactor;
        this.panel.repaint();
    }

    public BPos getPos(double mouseX, double mouseY) {
        Vec3i screenSize = this.getScreenSize();
        double x = (mouseX - screenSize.getX() / 2.0D - centerX) / screenSize.getX();
        double y = (mouseY - screenSize.getZ() / 2.0D - centerY) / screenSize.getZ();
        double blocksPerWidth = (screenSize.getX() / this.pixelsPerFragment) * (double) this.blocksPerFragment;
        double blocksPerHeight = (screenSize.getZ() / this.pixelsPerFragment) * (double) this.blocksPerFragment;
        x *= blocksPerWidth;
        y *= blocksPerHeight;
        int xi = (int) Math.round(x);
        int yi = (int) Math.round(y);
        return new BPos(xi, 0, yi);
    }

    public MapPanel getPanel() {
        return panel;
    }

    public enum ModifierDown {
        CTRL_DOWN(InputEvent::isControlDown),
        ALT_DOWN(InputEvent::isAltDown),
        META_DOWN(InputEvent::isMetaDown),
        SHIFT_DOWN(InputEvent::isShiftDown),
        ALT_GR_DOWN(InputEvent::isAltGraphDown),

        ;

        private final Function<InputEvent, Boolean> modifier;

        ModifierDown(Function<InputEvent, Boolean> modifier) {
            this.modifier = modifier;
        }

        public Function<InputEvent, Boolean> getModifier() {
            return modifier;
        }
    }

}
