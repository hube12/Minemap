package com.seedfinding.minemap.ui.dialog;

import com.seedfinding.mcbiome.biome.Biome;
import com.seedfinding.mcbiome.layer.BiomeLayer;
import com.seedfinding.minemap.MineMap;
import com.seedfinding.minemap.init.Logger;
import com.seedfinding.minemap.ui.map.MapContext;
import com.seedfinding.minemap.ui.map.MapManager;
import com.seedfinding.minemap.ui.map.MapPanel;
import com.seedfinding.minemap.util.ui.interactive.Prompt;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;
import java.util.*;

/**
 * Ctrl+F style popup: type a biome name (with auto-complete), press Enter and the map
 * jumps to the nearest matching biome around the current view center.
 */
public class BiomeSearchDialog extends JDialog {
    private static final int VISIBLE_ROWS = 8;
    private static final int MAX_RADIUS_BLOCKS = 20000;

    private final Runnable onExit;
    private final MapPanel map;
    private final List<Biome> allBiomes;
    private final JTextField input = new JTextField(24);
    private final DefaultListModel<Biome> model = new DefaultListModel<>();
    private final JList<Biome> suggestions = new JList<>(model);
    private final JLabel status = new JLabel(" ");
    private final JButton cancelButton = new JButton("Cancel");
    private SwingWorker<int[], Void> worker;
    private boolean searching = false;
    private boolean closed = false;

    public BiomeSearchDialog(MapPanel map, Runnable onExit) {
        super(MineMap.INSTANCE, false);
        this.map = map;
        this.onExit = onExit;
        this.allBiomes = new ArrayList<>(map.getContext().getSettings().getAllBiomes(
            Comparator.comparing(b -> display(b))));

        this.setUndecorated(true);
        this.setAlwaysOnTop(true);
        this.buildUi();
        this.refreshSuggestions();
        this.pack();
        this.setLocationRelativeTo(MineMap.INSTANCE);

        // Close when the popup loses focus (like a command palette).
        this.addWindowFocusListener(new WindowAdapter() {
            @Override
            public void windowLostFocus(WindowEvent e) {
                if (!searching) close();
            }
        });
    }

    public static String display(Biome biome) {
        StringBuilder sb = new StringBuilder();
        boolean upper = true;
        for (char c : biome.getName().toCharArray()) {
            if (c == '_') {
                sb.append(' ');
                upper = true;
            } else {
                sb.append(upper ? Character.toUpperCase(c) : c);
                upper = false;
            }
        }
        return sb.toString();
    }

    private void buildUi() {
        JPanel root = new JPanel(new BorderLayout(0, 6));
        root.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(new Color(90, 90, 90), 1),
            BorderFactory.createEmptyBorder(10, 12, 10, 12)));

        this.input.setFocusTraversalKeysEnabled(false); // let Tab auto-complete
        this.input.setFont(this.input.getFont().deriveFont(16f));
        Prompt.setPrompt("Search biome...  (Enter: jump, Esc: close)", this.input);
        this.input.setToolTipText("Search biome (Enter: jump to nearest, Up/Down: select, Esc: close)");
        this.input.getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) { refreshSuggestions(); }
            public void removeUpdate(DocumentEvent e) { refreshSuggestions(); }
            public void changedUpdate(DocumentEvent e) { refreshSuggestions(); }
        });

        this.suggestions.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        this.suggestions.setFocusable(false);
        this.suggestions.setCellRenderer(new DefaultListCellRenderer() {
            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean sel, boolean focus) {
                return super.getListCellRendererComponent(list, display((Biome) value), index, sel, false);
            }
        });
        this.suggestions.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                int i = suggestions.locationToIndex(e.getPoint());
                if (i >= 0) {
                    suggestions.setSelectedIndex(i);
                    startSearch();
                }
            }
        });

        this.status.setForeground(Color.GRAY);

        InputMap im = this.input.getInputMap(JComponent.WHEN_FOCUSED);
        ActionMap am = this.input.getActionMap();
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_DOWN, 0), "next");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_UP, 0), "prev");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_TAB, 0), "complete");
        im.put(KeyStroke.getKeyStroke(KeyEvent.VK_ENTER, 0), "go");
        am.put("next", action(() -> moveSelection(1)));
        am.put("prev", action(() -> moveSelection(-1)));
        am.put("complete", action(() -> {
            Biome b = suggestions.getSelectedValue();
            if (b != null) input.setText(display(b));
        }));
        am.put("go", action(this::startSearch));
        JRootPane rp = this.getRootPane();
        rp.getInputMap(JComponent.WHEN_IN_FOCUSED_WINDOW).put(KeyStroke.getKeyStroke(KeyEvent.VK_ESCAPE, 0), "close");
        rp.getActionMap().put("close", action(() -> { if (searching) cancelSearch(); else close(); }));

        root.add(this.input, BorderLayout.NORTH);
        this.suggestions.setVisibleRowCount(VISIBLE_ROWS);
        JScrollPane scroll = new JScrollPane(this.suggestions);
        scroll.setBorder(BorderFactory.createLineBorder(new Color(70, 70, 70)));
        scroll.setPreferredSize(new Dimension(320, 8 * 20 + 4));
        scroll.setFocusable(false);

        this.cancelButton.setFocusable(false);
        this.cancelButton.setVisible(false);
        this.cancelButton.addActionListener(e -> cancelSearch());
        JPanel south = new JPanel(new BorderLayout(8, 0));
        south.add(this.status, BorderLayout.CENTER);
        south.add(this.cancelButton, BorderLayout.EAST);

        root.add(scroll, BorderLayout.CENTER);
        root.add(south, BorderLayout.SOUTH);
        this.setContentPane(root);
    }

    private static Action action(Runnable r) {
        return new AbstractAction() {
            @Override
            public void actionPerformed(ActionEvent e) { r.run(); }
        };
    }

    private void moveSelection(int delta) {
        int n = this.model.size();
        if (n == 0) return;
        int i = Math.max(0, Math.min(n - 1, this.suggestions.getSelectedIndex() + delta));
        this.suggestions.setSelectedIndex(i);
        this.suggestions.ensureIndexIsVisible(i);
    }

    /** Prefix matches first, then word-prefix, then substring; all case-insensitive. */
    private void refreshSuggestions() {
        String q = this.input.getText().trim().toLowerCase(Locale.ROOT).replace('_', ' ');
        List<Biome> prefix = new ArrayList<>(), word = new ArrayList<>(), contains = new ArrayList<>();
        for (Biome b : this.allBiomes) {
            String n = display(b).toLowerCase(Locale.ROOT);
            if (q.isEmpty() || n.startsWith(q)) prefix.add(b);
            else if (n.contains(" " + q)) word.add(b);
            else if (n.contains(q)) contains.add(b);
        }
        this.model.clear();
        for (List<Biome> group : Arrays.asList(prefix, word, contains)) {
            for (Biome b : group) this.model.addElement(b);
        }
        if (!this.model.isEmpty()) {
            this.suggestions.setSelectedIndex(0);
            this.suggestions.ensureIndexIsVisible(0);
        }
        if (!this.searching) this.status.setText(this.model.isEmpty() ? "No matching biome" : " ");
    }

    private void startSearch() {
        if (this.searching) return;
        Biome target = this.suggestions.getSelectedValue();
        if (target == null) return;

        MapContext context = this.map.getContext();
        MapManager manager = this.map.getManager();
        int startX = manager.getCenterPos().getX(), startZ = manager.getCenterPos().getZ();
        int targetId = target.getId();

        this.searching = true;
        this.status.setText("Searching for " + display(target) + "...");
        this.cancelButton.setVisible(true);
        this.input.setEnabled(false);

        this.worker = new SwingWorker<int[], Void>() {
            @Override
            protected int[] doInBackground() {
                BiomeLayer layer = context.getBiomeLayer();
                int scale = Math.max(1, layer.getScale());
                // Expanding square rings; sample finely near the center, coarser far away.
                for (int r = 0; r <= MAX_RADIUS_BLOCKS; ) {
                    if (isCancelled()) return null;
                    int step = Math.max(scale, r < 1500 ? 8 : 32);
                    if (r == 0) {
                        if (layer.getBiome(Math.floorDiv(startX, scale), 0, Math.floorDiv(startZ, scale)) == targetId) return new int[]{startX, startZ};
                        r = step;
                        continue;
                    }
                    for (int d = -r; d <= r; d += step) {
                        if (hit(layer, scale, startX + d, startZ - r, targetId)) return new int[]{startX + d, startZ - r};
                        if (hit(layer, scale, startX + d, startZ + r, targetId)) return new int[]{startX + d, startZ + r};
                        if (hit(layer, scale, startX - r, startZ + d, targetId)) return new int[]{startX - r, startZ + d};
                        if (hit(layer, scale, startX + r, startZ + d, targetId)) return new int[]{startX + r, startZ + d};
                    }
                    r += step;
                    int shown = r;
                    SwingUtilities.invokeLater(() -> status.setText("Searching for " + display(target) + "... " + shown + " blocks"));
                }
                return null;
            }

            @Override
            protected void done() {
                if (isCancelled()) return; // cancelSearch()/close() already restored the UI
                searching = false;
                cancelButton.setVisible(false);
                input.setEnabled(true);
                input.requestFocusInWindow();
                try {
                    int[] pos = get();
                    if (pos == null) {
                        status.setText("No " + display(target) + " within " + MAX_RADIUS_BLOCKS + " blocks");
                        return;
                    }
                    manager.setCenterPos(pos[0], pos[1]);
                    close();
                } catch (Exception e) {
                    Logger.LOGGER.severe("Biome search failed: " + e);
                    status.setText("Search failed (see log)");
                }
            }
        };
        this.worker.execute();
    }

    private static boolean hit(BiomeLayer layer, int scale, int blockX, int blockZ, int targetId) {
        return layer.getBiome(Math.floorDiv(blockX, scale), 0, Math.floorDiv(blockZ, scale)) == targetId;
    }

    /** Stops a running search but keeps the popup open (Esc / Cancel button). */
    private void cancelSearch() {
        if (!this.searching) return;
        if (this.worker != null) this.worker.cancel(true);
        this.searching = false;
        this.cancelButton.setVisible(false);
        this.input.setEnabled(true);
        this.input.requestFocusInWindow();
        this.status.setText("Search cancelled");
    }

    public void open() {
        this.setVisible(true);
        this.input.requestFocusInWindow();
    }

    private void close() {
        if (this.closed) return;
        this.closed = true;
        if (this.worker != null) this.worker.cancel(true);
        this.setVisible(false);
        this.dispose();
        if (this.onExit != null) this.onExit.run();
    }
}
