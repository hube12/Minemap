package kaptainwutax.minemap.ui;

import javafx.scene.Node;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.control.TextArea;
import javafx.scene.layout.Pane;
import kaptainwutax.biomeutils.source.EndBiomeSource;
import kaptainwutax.biomeutils.source.NetherBiomeSource;
import kaptainwutax.biomeutils.source.OverworldBiomeSource;
import kaptainwutax.minemap.world.WorldInfo;
import kaptainwutax.seedutils.mc.MCVersion;

import javax.swing.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Random;

public class SeedTabs extends TabPane {

    private final Pane parent;
    public final ByteArrayOutputStream output = new ByteArrayOutputStream();

    public SeedTabs(Pane parent) {
        this.parent = parent;
        TextArea textArea = new TextArea();

        Tab console = new Tab("Console");
        console.setContent(textArea);

        PrintStream printStream = new PrintStream(this.output) {
            @Override
            public synchronized void println(String x) {
                SwingUtilities.invokeLater(() -> textArea.setText(textArea.getText() + x + "\n"));
            }
        };

        System.setOut(printStream);
        System.setErr(printStream);
    }

    public void loadSeed(MCVersion version, String worldSeed, int threadCount) {
        if(worldSeed.isEmpty()) {
            this.loadSeed(version, new Random().nextLong(), threadCount);
            return;
        }

        try {
            this.loadSeed(version, Long.parseLong(worldSeed), threadCount);
        } catch(NumberFormatException e) {
            this.loadSeed(version, worldSeed.hashCode(), threadCount);
        }
    }

    private void loadSeed(MCVersion version, long worldSeed, int threadCount) {
        String prefix = "[" + version + "] ";

        Tab overworld = new Tab(prefix + "Overworld " + worldSeed);
        Tab nether = new Tab(prefix + "Nether " + worldSeed);
        Tab end = new Tab(prefix + "End " + worldSeed);

        overworld.setContent(new MapPanel(this.parent, new WorldInfo(version, worldSeed,
                WorldInfo.QUARTER_RES_ID, OverworldBiomeSource::new), threadCount));

        nether.setContent(new MapPanel(this.parent, new WorldInfo(version, worldSeed,
                WorldInfo.QUARTER_RES_ID, NetherBiomeSource::new), threadCount));

        end.setContent(new MapPanel(this.parent, new WorldInfo(version, worldSeed,
                WorldInfo.QUARTER_RES_ID, EndBiomeSource::new), threadCount));

        this.getTabs().addAll(overworld, nether, end);
    }

    public synchronized void invalidateAll() {
        for(Tab tab: this.getTabs()) {
            Node content = tab.getContent();
            if(!(content instanceof MapPanel))continue;
            ((MapPanel)content).invalidate();
        }
    }

}
