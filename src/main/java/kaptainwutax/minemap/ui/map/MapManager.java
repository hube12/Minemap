package kaptainwutax.minemap.ui.map;

import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.mathutils.util.Mth;
import kaptainwutax.mcutils.util.data.Pair;
import kaptainwutax.mcutils.util.math.Vec3i;
import kaptainwutax.mcutils.util.pos.BPos;
import kaptainwutax.mcutils.util.pos.RPos;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.Logger;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.dialog.RenameTabDialog;
import kaptainwutax.minemap.ui.map.fragment.Fragment;
import kaptainwutax.minemap.ui.map.interactive.Chest;
import kaptainwutax.minemap.ui.map.tool.*;

import javax.swing.*;
import javax.swing.border.EmptyBorder;
import javax.swing.event.PopupMenuEvent;
import javax.swing.event.PopupMenuListener;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;

public class MapManager {

    public static final int DEFAULT_REGION_SIZE = 512;//8192;
    public static final double DEFAULT_PIXELS_PER_FRAGMENT = 256.0;
    public int blocksPerFragment;
    public final ArrayList<Tool> toolsList = new ArrayList<>();
    private final MapPanel panel;
    private final JPopupMenu popup;
    private final Chest chestMenu;
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
            BPos pos = this.getPos(e.getX(), e.getY());
            int x = pos.getX();
            int z = pos.getZ();
            this.panel.scheduler.forEachFragment(fragment -> fragment.onHovered(pos.getX(), pos.getZ()));

            SwingUtilities.invokeLater(() -> {
                this.panel.leftBar.tooltip.updateBiomeDisplay(x, z);
                this.panel.leftBar.tooltip.tooltip.repaint();
                this.panel.repaint();
            });

        }));

        this.panel.addMouseListener(Events.Mouse.onPressed(e -> {
            if (SwingUtilities.isLeftMouseButton(e)) {
                this.mousePointer = e.getPoint();
                BPos pos = this.getPos(e.getX(), e.getY());
                this.panel.scheduler.forEachFragment(fragment -> fragment.onClicked(pos.getX(), pos.getZ()));
                if (selectedTool == null) {
                    this.panel.setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                } else {
                    // if tool has no more points to it
                    if (!selectedTool.addPoint(pos)) {
                        selectedTool = selectedTool.duplicate();
                        toolsList.add(selectedTool);
                        selectedTool.addPoint(pos);
                    }
                    this.panel.rightBar.tooltip.updateToolsMetrics(toolsList);
                }
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

        JMenuItem pin = new JMenuItem("Pin");
        pin.setBorder(new EmptyBorder(5, 15, 5, 15));

        pin.addMouseListener(Events.Mouse.onReleased(e -> {
            boolean newState = !MineMap.INSTANCE.worldTabs.getSelectedHeader().isPinned();
            MineMap.INSTANCE.worldTabs.getSelectedHeader().setPinned(newState);
            pin.setText(newState ? "Unpin" : "Pin");
        }));

        JMenuItem rename = new JMenuItem("Rename");
        rename.setBorder(new EmptyBorder(5, 15, 5, 15));

        rename.addMouseListener(Events.Mouse.onReleased(e -> {
            RenameTabDialog renameTabDialog = new RenameTabDialog();
            renameTabDialog.setVisible(true);
        }));

        JMenuItem settings = new JMenuItem("Settings");
        settings.setBorder(new EmptyBorder(5, 15, 5, 15));
        settings.addMouseListener(Events.Mouse.onReleased(e -> this.panel.leftBar.settings.setVisible(!panel.leftBar.settings.isVisible())));

        JMenuItem chest = new JMenuItem("Chest");
        chest.setBorder(new EmptyBorder(5, 15, 5, 15));
        chestMenu = new Chest(this.panel);
        chest.addMouseListener(Events.Mouse.onReleased(e -> this.chestMenu.setVisible(true)));

        popup.add(pin);
        popup.add(rename);
        popup.add(settings);
        List<Supplier<Tool>> tools = Arrays.asList(Ruler::new, Area::new, Circle::new, Polyline::new);
        this.addTools(popup, tools);
        popup.addPopupMenuListener(new PopupMenuListener() {
                                       @Override
                                       public void popupMenuWillBecomeVisible(final PopupMenuEvent e) {
                                           popup.removeAll();
                                           MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
                                           ArrayList<Pair<Feature<?, ?>, List<BPos>>> features = new ArrayList<>();
                                           int size = (int) map.manager.pixelsPerFragment;
                                           JPopupMenu source = (JPopupMenu) e.getSource();
                                           try {
                                               Field desiredLocationXField = JPopupMenu.class.getDeclaredField("desiredLocationX");
                                               Field desiredLocationYField = JPopupMenu.class.getDeclaredField("desiredLocationY");
                                               desiredLocationXField.setAccessible(true);
                                               desiredLocationYField.setAccessible(true);
                                               // 10 and 70 are fixed from the window on Microsoft (assumimg correct header)
                                               int desiredLocationX = (int) desiredLocationXField.get(source) - MineMap.INSTANCE.getX() - map.manager.panel.getX() - 10;
                                               int desiredLocationY = (int) desiredLocationYField.get(source) - MineMap.INSTANCE.getY() - map.manager.panel.getY() - 70;
                                               BPos bPos = getPos(desiredLocationX, desiredLocationY);
                                               RPos rPos = bPos.toRegionPos(map.manager.blocksPerFragment);
                                               Fragment fragment = map.scheduler.getFragmentAt(rPos.getX(), rPos.getZ());
                                               fragment.getHoveredFeatures(size, size).forEach((feature, positions) -> {
                                                   if (!positions.isEmpty() && feature instanceof RegionStructure<?, ?>) {
                                                       features.add(new Pair<>(feature, positions));
                                                   }
                                               });
                                               if (features.isEmpty()) {
                                                   for (int i = -1; i <= 1; i++) {
                                                       for (int j = -1; j <= 1; j++) {
                                                           RPos offsetRpos = new RPos(rPos.getX() + i, rPos.getZ() + j, map.manager.blocksPerFragment);
                                                           map.scheduler.getFragmentAt(offsetRpos.getX(), offsetRpos.getZ()).getHoveredFeatures(size, size).forEach((feature, positions) -> {
                                                               if (!positions.isEmpty() && feature instanceof RegionStructure<?, ?>) {
                                                                   features.add(new Pair<>(feature, positions));
                                                               }
                                                           });
                                                       }
                                                   }
                                               }
                                           } catch (Exception reflectionException) {
                                               Logger.LOGGER.warning("Reflection failed with error " + reflectionException.getMessage());
                                               map.scheduler.forEachFragment(fragment -> {
                                                   fragment.getHoveredFeatures(size, size).forEach((feature, positions) -> {
                                                       if (!positions.isEmpty() && feature instanceof RegionStructure<?, ?>) {
                                                           features.add(new Pair<>(feature, positions));
                                                       }
                                                   });
                                               });
                                           }
                                           if (!features.isEmpty()) {
                                               popup.add(chest);
                                               Pair<Feature<?, ?>, List<BPos>> featureListPair = features.get(0);
                                               Feature<?, ?> feature = featureListPair.getFirst();
                                               BPos bPos = featureListPair.getSecond().get(0);
                                               JMenuItem copyTp = new JMenuItem("Copy TP");
                                               copyTp.addMouseListener(Events.Mouse.onReleased(ecp -> {
                                                       Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                                                       String tpCommand = String.format("/tp @p %d ~ %d", bPos.getX(), bPos.getZ());
                                                       StringSelection stringSelection = new StringSelection(tpCommand);
                                                       clipboard.setContents(stringSelection, null);
                                                   }
                                               ));
                                               popup.add(copyTp);
                                               chestMenu.setPos(bPos.toChunkPos());
                                               chestMenu.setFeature((RegionStructure<?, ?>) feature);
                                               chestMenu.generateContent();
                                           } else {
                                               popup.add(pin);
                                               popup.add(rename);
                                               popup.add(settings);
                                               addTools(popup, tools);
                                           }
                                       }

                                       @Override
                                       public void popupMenuWillBecomeInvisible(PopupMenuEvent e) {
                                       }

                                       @Override
                                       public void popupMenuCanceled(PopupMenuEvent e) { }
                                   }
        );
        this.panel.setComponentPopupMenu(popup);
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
                    newPixelsPerFragment /= 4.0D;
                } else {
                    newPixelsPerFragment *= 4.0D;
                }

                if (newPixelsPerFragment > 4096.0D * (double) manager.blocksPerFragment / DEFAULT_REGION_SIZE) {
                    // restrict min zoom to 4096 chunks per fragment
                    newPixelsPerFragment = 4096.0D * (manager.blocksPerFragment / 512.0D);
                }else if (Configs.USER_PROFILE.getUserSettings().restrictMaximumZoom && newPixelsPerFragment < 32.0D * (double) manager.blocksPerFragment / DEFAULT_REGION_SIZE) {
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
        this.panel.rightBar.tooltip.updateToolsMetrics(toolsList);
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
