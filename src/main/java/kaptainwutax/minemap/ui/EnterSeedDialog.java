package kaptainwutax.minemap.ui;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import kaptainwutax.minemap.MineMap;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.seedutils.mc.MCVersion;

import java.util.Arrays;
import java.util.stream.IntStream;

public class EnterSeedDialog extends Dialog<Boolean> {

    public EnterSeedDialog() {
        DialogPane dialogPane = new DialogPane();
        BorderPane pane = new BorderPane();

        Label enterSeed = new Label("Enter your seed here:");
        TextField seedField = new TextField();

        int cores = Runtime.getRuntime().availableProcessors();
        Dropdown<MCVersion> versionDropdown = new Dropdown<>(MCVersion::toString, Arrays.stream(MCVersion.values()).filter(v -> v.isNewerOrEqualTo(MCVersion.v1_13)));
        Dropdown<Integer> threadDropdown = new Dropdown<>(i -> i + (i == 1 ? " thread" : " threads"), IntStream.rangeClosed(1, cores).boxed());
        versionDropdown.selectIfPresent(Configs.USER_PROFILE.getVersion());
        threadDropdown.selectIfPresent(Configs.USER_PROFILE.getThreadCount(cores));
        SplitPane dropdowns = new SplitPane(versionDropdown, threadDropdown);

        Button yesButton = new Button("Continue");

        yesButton.setOnMouseClicked(e -> {
            Platform.runLater(() -> {
                MCVersion version = versionDropdown.getSelected();
                int threadCount = threadDropdown.getSelected();
                MineMap.INSTANCE.seedTabs.loadSeed(version, seedField.getText(), threadCount);
                Configs.USER_PROFILE.setVersion(version);
                Configs.USER_PROFILE.setThreadCount(threadCount);
                this.yeet();
            });
        });

        BorderPane top = new BorderPane();
        top.setTop(enterSeed);
        top.setCenter(seedField);

        pane.setTop(top);
        pane.setCenter(dropdowns);
        pane.setBottom(yesButton);

        dialogPane.getChildren().add(pane);
        this.setDialogPane(dialogPane);
        this.setResizable(true);
        this.getDialogPane().getScene().getWindow().setOnCloseRequest(event -> this.yeet());

        //TODO: sigh...
        versionDropdown.setMinWidth(120);
        threadDropdown.setMinWidth(120);
        this.getDialogPane().setMinHeight(200);
    }

    private void yeet() {
        this.setResult(Boolean.TRUE);
        this.close();
    }

}
