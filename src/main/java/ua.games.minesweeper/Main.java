package ua.games.minesweeper;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.StageStyle;

public class Main extends Application {

    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage stage) throws Exception {
        String fxmlFile = "/fxml/main.fxml";
        FXMLLoader loader = new FXMLLoader();
        Parent root = loader.load(getClass().getResourceAsStream(fxmlFile));
        stage.setTitle("Minesweeper");
        stage.initStyle(StageStyle.UTILITY);
    //    stage.getIcons().add(new Image(getClass().getResourceAsStream("/images/minesweeper_logo.bmp")));
        stage.setScene(new Scene(root));
        stage.setResizable(true);
        stage.show();

        //  stage.widthProperty().addListener((obs, oldVal, newVal) -> {
//            System.out.println("oldWidth: "  + oldVal + ", new: " + newVal);
//        });
//
//        stage.heightProperty().addListener((obs, oldVal, newVal) -> {
//            System.out.println("oldHeight: "  + oldVal + ", new: " + newVal);
//        });
    }
}