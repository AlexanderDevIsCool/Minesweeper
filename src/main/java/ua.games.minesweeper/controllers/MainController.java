package ua.games.minesweeper.controllers;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import sun.awt.PlatformFont;
import ua.games.minesweeper.Main;
import ua.games.minesweeper.bot.Bot;
import ua.games.minesweeper.service.BombsCounter;
import ua.games.minesweeper.service.Game;
import ua.games.minesweeper.service.GameMode;
import ua.games.minesweeper.service.Palette;
import ua.games.minesweeper.service.RecordsFuller;
import ua.games.minesweeper.service.Smile;
import ua.games.minesweeper.service.Timer;

import javax.swing.*;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {
    @FXML
    private BorderPane mainPane;

    @FXML
    private VBox topBox;

    @FXML
    private TextField customHeight, customWidth, customMines;

    @FXML
    private Label beginner_name, intermediate_name, expert_name,
            beginner_time, intermediate_time, expert_time;

    private GameMode gameMode;

    private Game game = Game.getGame();

    private boolean isMainWindow = true;

    private RecordsFuller recordsFuller = new RecordsFuller();

    Bot bot;

    @FXML
    private HBox bombsBox, smileBox, timerBox;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        mainPane.setStyle("-fx-background-color: silver");
        if (isMainWindow) {
            isMainWindow = false;
            gameMode = GameMode.Beginner;
            createGame(gameMode.getWidth(), gameMode.getHeight(), gameMode.getMines());

            HBox hbox = (HBox) topBox.getChildren().get(1);
            hbox.setStyle("-fx-background-color: silver");

            BombsCounter bombsCounter = BombsCounter.getBombsCounter(gameMode.getMines());
            Smile smile = Smile.getSmile();
            Timer timer = Timer.getTimer();

            timerBox.setAlignment(Pos.BASELINE_RIGHT);
            bombsBox.getChildren().add(bombsCounter);
            smileBox.getChildren().add(smile);
            timerBox.getChildren().add(timer);
        }
    }

    @FXML
    public void onClickMenuItemBeginner(ActionEvent event) {
        gameMode = GameMode.Beginner;
        createGame(gameMode.getWidth(), gameMode.getHeight(), gameMode.getMines());
    }

    @FXML
    public void onClickMenuItemIntermediate(ActionEvent event) {
        gameMode = GameMode.Intermediate;
        createGame(gameMode.getWidth(), gameMode.getHeight(), gameMode.getMines());
    }

    @FXML
    public void onClickMenuItemExpert(ActionEvent event) {
        gameMode = GameMode.Expert;
        createGame(gameMode.getWidth(), gameMode.getHeight(), gameMode.getMines());
    }

    @FXML
    public void onClickMenuItemCustom(ActionEvent event) {
        openWindow("custom.fxml", "Custom");
    }

    @FXML
    public void onClickMenuItemBestTimes(ActionEvent event) {
        openWindow("best_times.fxml", "Best times");
        Platform.runLater(() -> {
            beginner_name.setText(recordsFuller.getName(GameMode.Beginner));
            beginner_time.setText(String.valueOf(recordsFuller.getRecord(GameMode.Beginner)));
            intermediate_name.setText(recordsFuller.getName(GameMode.Intermediate));
            intermediate_time.setText(String.valueOf(recordsFuller.getRecord(GameMode.Intermediate)));
            expert_name.setText(recordsFuller.getName(GameMode.Expert));
            expert_time.setText(String.valueOf(recordsFuller.getRecord(GameMode.Expert)));
        });
    }

    @FXML
    public void onClickRestBestTimes(ActionEvent event){
        recordsFuller.resetBestRecords();
        Stage stage = (Stage)beginner_time.getScene().getWindow();
        stage.close();
    }

    @FXML
    public void onClickCloseBestTimes(ActionEvent event){
        Stage stage = (Stage)beginner_time.getScene().getWindow();
        stage.close();
    }

    private void openWindow(String fxml, String title) {
        Platform.runLater(() -> {
            try {
                Stage stage = new Stage();
                FXMLLoader fxmlLoader = new FXMLLoader(
                        MainController.this.getClass().getResource("/fxml/" + fxml));
                fxmlLoader.setController(MainController.this);
                Parent root = fxmlLoader.load();
                stage.setScene(new Scene(root));
                stage.setTitle(title);
                stage.initModality(Modality.WINDOW_MODAL);
                stage.initStyle(StageStyle.UTILITY);
                stage.initOwner(
                        (mainPane.getScene().getWindow()));
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @FXML
    public void onClickMenuItemClose(ActionEvent event) {
        System.exit(0);
    }

    @FXML
    public void onClickMenuItemNew(ActionEvent event) {
        createGame(game.getWidth(), game.getHeight(), game.getMinesQuantity());
    }


    @FXML
    public void onClickCustomButtonOK(ActionEvent event) {
        int height, width, mines;

        Stage stage = (Stage) customHeight.getScene().getWindow();

        height = Integer.parseInt(customHeight.getText());
        width = Integer.parseInt(customWidth.getText());
        mines = Integer.parseInt(customMines.getText());

        stage.close();

        gameMode = null;
        createGame(width, height, mines);
    }

    @FXML
    public void onClickCustomButtonCancel(ActionEvent event) {
        Stage stage = (Stage) customHeight.getScene().getWindow();
        stage.close();
    }

    public void createGame(int width, int height, int mines) {
        if (gameMode == null)
            game.gameStart(width, height, mines);
        else
            game.gameStart(gameMode);
        Platform.runLater(() -> {
            mainPane.setCenter(Palette.getPalette());

            if (gameMode == GameMode.Intermediate || gameMode == GameMode.Expert) {
                mainPane.getScene().getWindow().setWidth(20 * width + 20 + 20);
                mainPane.getScene().getWindow().setHeight(20 * height + topBox.getHeight() + 12 + 35);
            }

            mainPane.getScene().getWindow().setWidth(20 * width + 20);
            mainPane.getScene().getWindow().setHeight(20 * height + topBox.getHeight() + 12 + 35);
        });
    }

    @FXML
    public void onClickMenuItemBot(ActionEvent event) {
        createGame(gameMode.getWidth(), gameMode.getHeight(), gameMode.getMines());
        bot = new Bot(gameMode);
        bot.start();
    }
}
