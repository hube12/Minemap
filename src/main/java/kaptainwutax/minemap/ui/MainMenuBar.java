package kaptainwutax.minemap.ui;

import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import kaptainwutax.minemap.init.Configs;

public class MainMenuBar extends MenuBar {

    public MainMenuBar() {
        //FILE
        Menu fileMenu = this.createMenu("File");
        MenuItem newFromSeed = new MenuItem("New From Seed...");

        newFromSeed.setOnAction(e -> {
            try {
                EnterSeedDialog dialog = new EnterSeedDialog();
                dialog.show();
            } catch(Exception _e) {
                _e.printStackTrace();
            }
        });

        fileMenu.getItems().add(newFromSeed);

        //STYLE
        Menu styleMenu = this.createMenu("Style");

        for(String style: Configs.BIOME_COLORS.getStyles()) {
            MenuItem styleItem = new MenuItem(style);

            styleItem.setOnAction(e -> {
                styleMenu.getItems().forEach(i -> i.setDisable(false));
                styleItem.setDisable(true);
                Configs.USER_PROFILE.setStyle(style);
            });

            if(Configs.USER_PROFILE.getStyle().equals(style)) {
                styleItem.setDisable(true);
            }

            styleMenu.getItems().add(styleItem);
        }
    }

    public Menu createMenu(String title, MenuItem... items) {
        Menu menu = new Menu(title);
        menu.getItems().addAll(items);
        this.getMenus().add(menu);
        return menu;
    }

}
