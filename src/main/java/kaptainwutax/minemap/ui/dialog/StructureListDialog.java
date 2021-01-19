package kaptainwutax.minemap.ui.dialog;

import kaptainwutax.biomeutils.source.BiomeSource;
import kaptainwutax.featureutils.Feature;
import kaptainwutax.featureutils.structure.RegionStructure;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.feature.OWBastionRemnant;
import kaptainwutax.minemap.feature.OWFortress;
import kaptainwutax.minemap.feature.StructureHelper;
import kaptainwutax.minemap.init.Icons;
import kaptainwutax.minemap.listener.Events;
import kaptainwutax.minemap.ui.component.Dropdown;
import kaptainwutax.minemap.ui.map.MapContext;
import kaptainwutax.minemap.ui.map.MapManager;
import kaptainwutax.minemap.ui.map.MapPanel;
import kaptainwutax.minemap.ui.map.MapSettings;
import kaptainwutax.minemap.util.data.Str;
import kaptainwutax.minemap.util.ui.*;
import kaptainwutax.minemap.util.ui.buttons.CopyButton;
import kaptainwutax.minemap.util.ui.buttons.JumpButton;
import kaptainwutax.seedutils.mc.ChunkRand;
import kaptainwutax.seedutils.mc.Dimension;
import kaptainwutax.seedutils.mc.pos.BPos;
import org.jdesktop.swingx.prompt.PromptSupport;

import javax.swing.*;
import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class StructureListDialog extends Dialog {
    public Dropdown<StructureItem> structureItemDropdown;
    public JButton continueButton;
    public JTextField enterN;

    public StructureListDialog() {
        super("List N closest structures", new GridLayout(0, 1));
    }

    @Override
    public void initComponents() {
        MapPanel map = MineMap.INSTANCE.worldTabs.getSelectedMapPanel();
        if (map == null) return;

        MapContext context = map.getContext();
        MapSettings settings = context.getSettings();
        MapManager manager = map.getManager();
        ChunkRand chunkRand = new ChunkRand();
        List<Feature<?, ?>> features = settings.getAllFeatures();

        List<StructureItem> structureItems = features.stream()
                .filter(e -> e instanceof RegionStructure)
                .map(e -> new StructureItem((RegionStructure<?, ?>) e))
                .collect(Collectors.toList());

        this.structureItemDropdown = new Dropdown<>(structureItems);

        this.enterN = new JTextField("1");
        PromptSupport.setPrompt("Number of structures", this.enterN);

        this.enterN.addKeyListener(Events.Keyboard.onReleased(e -> {
            try {
                Integer.parseInt(this.enterN.getText().trim());
                this.continueButton.setEnabled(true);
            } catch (Exception _e) {
                this.continueButton.setEnabled(false);
            }
        }));

        this.continueButton = new JButton();
        this.continueButton.setText("Continue");

        this.continueButton.addMouseListener(Events.Mouse.onPressed(e -> {
            if (!this.isEnabled()) return;

            int n;
            try {
                n = Integer.parseInt(this.enterN.getText().trim());
            } catch (NumberFormatException _e) {
                JOptionPane.showMessageDialog(this, String.format("This is not a number: %s", this.enterN.getText().trim()));
                return;
            }

            if (n > 300 || n <= 0) {
                JOptionPane.showMessageDialog(this, String.format("You have chosen a number (%d) outside of the permitted range [1;300]", n));
                return;
            }

            RegionStructure<?, ?> feature = this.structureItemDropdown.getSelected().getFeature();
            BPos centerPos = manager.getCenterPos();
            BiomeSource biomeSource = context.getBiomeSource();
            int dimCoeff = 0;
            if (feature instanceof OWBastionRemnant || feature instanceof OWFortress) {
                biomeSource = context.getBiomeSource(Dimension.NETHER);
                dimCoeff = 3;
            }

            List<BPos> bPosList = StructureHelper.getClosest(feature, centerPos, context.worldSeed, chunkRand, biomeSource, dimCoeff)
                    .sequential()
                    .limit(n)
                    .collect(Collectors.toList());

            // destroy the current container
            this.dispose();

            // create a new frame
            JFrame frame = new JFrame(String.format("List of %d %s", n, feature.getName()));
            frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
            frame.setPreferredSize(new java.awt.Dimension(500, 80 + Math.min(350, 70 * bPosList.size())));

            // create the inner list
            final ListPanel listPanel = new ListPanel();
            bPosList.forEach(bPos -> listPanel.addPanel(new Entry(feature, bPos)));

            JButton copyTPs = new JButton("Copy all TPs");
            copyTPs.addActionListener(event->{
                StringBuilder copyString=new StringBuilder();
                bPosList.forEach(bPos -> copyString.append(String.format("/tp @p %d ~ %d", bPos.getX(),bPos.getZ())).append("\n"));
                StringSelection stringSelection = new StringSelection(copyString.toString());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                copyTPs.setBackground(new Color(50, 255, 84));
                copyTPs.setForeground(Color.WHITE);
            });

            JButton copyLocations = new JButton("Copy all locations");
            copyLocations.addActionListener(event->{
                StringBuilder copyString=new StringBuilder(String.format("%s\nposX,posZ\n", feature.getName()));
                bPosList.forEach(bPos -> copyString.append(String.format("%d,%d", bPos.getX(),bPos.getZ())).append("\n"));
                StringSelection stringSelection = new StringSelection(copyString.toString());
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                copyLocations.setBackground(new Color(50, 255, 84));
                copyLocations.setForeground(Color.WHITE);
            });

            JSplitPane copyPane=new JSplitPane(JSplitPane.HORIZONTAL_SPLIT,copyTPs,copyLocations);
            copyPane.setResizeWeight(1.0);
            listPanel.add(copyPane, BorderLayout.SOUTH);

            frame.add(listPanel);

            // display it
            frame.pack();
            frame.setLocationRelativeTo(null); // center
            frame.setVisible(true);
        }));

        this.getContentPane().add(this.enterN);
        this.getContentPane().add(this.structureItemDropdown);
        this.getContentPane().add(this.continueButton);
    }


    static class StructureItem {

        private final RegionStructure<?, ?> feature;

        StructureItem(RegionStructure<?, ?> feature) {
            this.feature = feature;
        }

        public RegionStructure<?, ?> getFeature() {
            return feature;
        }

        @Override
        public String toString() {
            return feature.getName();
        }
    }

    static class Entry extends RoundedPanel {
        private final JComponent iconView;
        private final JLabel positionText;
        private final CopyButton copyCoordinate;
        private final JumpButton jumpCoordinate;

        public Entry(Feature<?, ?> feature, BPos pos) {
            this.setLayout(new GridBagLayout());
            GridBagConstraints gbc = new GridBagConstraints();
            gbc.insets = new Insets(3, 3, 3, 3);

            this.iconView = new JComponent() {
                @Override
                public java.awt.Dimension getPreferredSize() {
                    return new java.awt.Dimension(30, 30);
                }

                @Override
                protected void paintComponent(Graphics g) {
                    super.paintComponent(g);
                    BufferedImage icon = Icons.REGISTRY.get(feature.getClass());
                    int iconSizeX, iconSizeZ;
                    int defaultValue = 20;
                    float factor = 1.5F;
                    if (icon.getRaster().getWidth() > icon.getRaster().getHeight()) {
                        iconSizeX = defaultValue;
                        iconSizeZ = (int) (defaultValue * (float) icon.getRaster().getHeight() / icon.getRaster().getWidth());
                    } else {
                        iconSizeZ = defaultValue;
                        iconSizeX = (int) (defaultValue * (float) icon.getRaster().getWidth() / icon.getRaster().getHeight());
                    }
                    g.drawImage(icon, (defaultValue - iconSizeX) / 2, (defaultValue - iconSizeZ) / 2, (int) (iconSizeX * factor), (int) (iconSizeZ * factor), null);
                }
            };

            this.positionText = new JLabel(" [" + pos.getX() + ", " + pos.getZ() + "] " + Str.formatName(feature.getName()));
            this.positionText.setFont(new Font(this.positionText.getFont().getName(), Font.PLAIN, 18));
            this.positionText.setBackground(new Color(0, 0, 0, 0));
            this.positionText.setFocusable(false);
            this.positionText.setOpaque(true);
            this.positionText.setForeground(Color.WHITE);

            this.copyCoordinate = new CopyButton(16, 6, 1.0F,true,Color.DARK_GRAY);
            this.copyCoordinate.addActionListener(e->{
                String myString = String.format("/tp @p %d ~ %d", pos.getX(),pos.getZ());
                StringSelection stringSelection = new StringSelection(myString);
                Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
                clipboard.setContents(stringSelection, null);
                ListPanel.toggleParentChildren(2,this, component -> component.copyCoordinate.changeBColor(Color.DARK_GRAY));
                this.copyCoordinate.changeBColor(new Color(50, 255, 84));
            });

            this.jumpCoordinate = new JumpButton(16, 6, 1.0F,true,Color.DARK_GRAY);
            this.jumpCoordinate.addActionListener(e->{
                MineMap.INSTANCE.worldTabs.getSelectedMapPanel().manager.setCenterPos(pos.getX(), pos.getZ());
                ListPanel.toggleParentChildren(2,this, component -> component.jumpCoordinate.changeBColor(Color.DARK_GRAY));
                this.jumpCoordinate.changeBColor(new Color(50, 255, 84));
            });

            this.add(this.iconView, gbc);
            this.add(this.positionText, gbc);
            this.add(this.jumpCoordinate, gbc);
            this.add(this.copyCoordinate, gbc);

            this.setBackground(new Color(0, 0, 0, 180));
        }


    }


}