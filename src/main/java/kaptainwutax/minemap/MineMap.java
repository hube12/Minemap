package kaptainwutax.minemap;

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import kaptainwutax.minemap.init.Configs;
import kaptainwutax.minemap.ui.MainMenuBar;
import kaptainwutax.minemap.ui.SeedTabs;

import java.awt.*;

public class MineMap extends Application {

    public static MineMap INSTANCE;
    public SeedTabs seedTabs;
    private MainMenuBar menuBar;

    /**
     * Don't launch this!
     * @see Main#main(String[])
     * */
    public static void main(String[] args) {
        Configs.registerConfigs();
        Application.launch(args);
    }

    @Override
    public void start(Stage stage) {
        INSTANCE = this;
        stage.setTitle("MineMap");

        BorderPane pane = new BorderPane();
        this.initComponents(pane);

        Dimension screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        Scene scene = new Scene(pane, screenSize.width / 2.0D, screenSize.height / 2.0D);
        stage.setMaximized(true);

        stage.setScene(scene);
        stage.show();
    }

    private void initComponents(BorderPane pane) {
        this.menuBar = new MainMenuBar();
        this.seedTabs = new SeedTabs(pane);

        pane.setTop(menuBar);
        pane.setCenter(this.seedTabs);
    }

}
