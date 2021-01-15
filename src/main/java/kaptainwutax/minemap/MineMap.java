package kaptainwutax.minemap;

import com.formdev.flatlaf.*;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.init.Features;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.ui.MenuBar;
import kaptainwutax.minemap.ui.component.WorldTabs;
import kaptainwutax.seedutils.mc.seed.WorldSeed;

import javax.swing.*;
import java.awt.*;
import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.Arrays;
import java.util.function.Consumer;
import java.util.function.Supplier;

public class MineMap extends JFrame {

    public static MineMap INSTANCE;
    public static LookType lookType = LookType.DARCULA;

    public JMenuBar toolbarPane;
    public WorldTabs worldTabs;

    public static void main(String[] args) {
        Features.registerFeatures();
        Icons.registerIcons();
        Configs.registerConfigs();

        INSTANCE = new MineMap();
        INSTANCE.setVisible(true);
    }

    public MineMap() {
        applyStyle();
        BorderLayout layout = new BorderLayout();
        this.setLayout(layout);
        this.initComponents();
        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        this.setExtendedState(JFrame.MAXIMIZED_BOTH);
        this.setSize(screenSize.width / 2, screenSize.height / 2);
        this.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        this.setTitle("MineMap");
        System.out.println("Hello, its me");
        System.out.println("General Kenobi");
    }

    private void initComponents() {
        this.toolbarPane = new MenuBar();
        this.add(this.toolbarPane, BorderLayout.NORTH);

        this.worldTabs = new WorldTabs();
        this.add(this.worldTabs);
    }

    public static void applyStyle(){
        try {
            lookType.setLookAndFeel();
        }catch (Exception e){
            lookType=LookType.DARCULA;
            try {
                lookType.setLookAndFeel();
            }catch (Exception impossibleError){
                impossibleError.printStackTrace();
            }
        }
    }

    public enum LookType {
        DARK("Dark", FlatDarkLaf::new),
        LIGHT("Light", FlatLightLaf::new),
        INTELLIJ("Intellij", FlatIntelliJLaf::new),
        DARCULA("Darcula", FlatDarculaLaf::new);

        private final String name;
        private final Supplier<FlatLaf> supplier;

        LookType(String name, Supplier<FlatLaf> supplier) {
            this.name = name;
            this.supplier = supplier;
        }

        public void setLookAndFeel() throws UnsupportedLookAndFeelException {
            UIManager.setLookAndFeel(supplier.get());
            for(Window window : JFrame.getWindows()) {
                SwingUtilities.updateComponentTreeUI(window);
            }
        }

        public String getName() {
            return name;
        }

        public boolean isDark() {
            return supplier.get().isDark();
        }
    }

}
