package ua.games.minesweeper.service;

import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.layout.GridPane;

public class Game {
    private Palette palette = Palette.getPalette();
    private Timer timer = Timer.getTimer();
    private Smile smile = Smile.getSmile();
    private BombsCounter bombsCounter;
    private RecordsFuller recordsFuller = new RecordsFuller();
    private GameMode gameMode;

    private boolean isWin = false;
    private boolean isGameAlive = false;
    private boolean isFirstClick = true;

    private int x_saved = 0;
    private int y_saved = 0;

    private int minesQuantity;

    private int width;
    private int height;

    private static Game game;

    private Game() {

    }

    public static Game getGame() {
        if (game == null)
            game = new Game();
        return game;
    }

    public void gameStart(GameMode gameMode) {
        this.gameMode = gameMode;
        isGameAlive = true;
        isWin = false;
        minesQuantity = gameMode.getMines();
        bombsCounter = BombsCounter.getBombsCounter(gameMode.getMines());
        bombsCounter.setText(String.valueOf(minesQuantity));
        this.width = gameMode.getWidth();
        this.height = gameMode.getHeight();
        palette.render(gameMode.getWidth(), gameMode.getHeight(), gameMode.getMines());
        setMinesWeeperButtonsEvents();
        setSmileButtonEvents();
        isFirstClick = true;
    }

    public void gameStart(int width, int height, int mines) {
        isGameAlive = true;
        isWin = false;
        minesQuantity = mines;
        bombsCounter = BombsCounter.getBombsCounter(mines);
        bombsCounter.setText(String.valueOf(mines));
        this.width = width;
        this.height = height;
        palette.render(width, height, mines);
        setMinesWeeperButtonsEvents();
        setSmileButtonEvents();
        isFirstClick = true;
    }

    public int getWidth() {
        return width;
    }

    public int getHeight() {
        return height;
    }

    public boolean isGameAlive() {
        return isGameAlive;
    }

    public void gameOver() {
        isGameAlive = false;
        if (isWin) {
            if (gameMode != null && (Integer.parseInt(timer.toString()) < recordsFuller.getRecord(gameMode)))
                openNewRecordWindow();
            smile.setGraphic(new ImageView(new Image("/images/win_smile.bmp")));
        } else {
            smile.setGraphic(new ImageView(new Image("/images/dead_smile.bmp")));
        }
        if (timer.isRunning())
            timer.stopTimer();
        removeEventsFromButtons();
        palette.openAllBombs(isWin);
    }

    private void openNewRecordWindow() {
        Dialog<String> dialog = new Dialog<>();
        dialog.setTitle("New best record");
        dialog.getDialogPane().getButtonTypes().addAll(ButtonType.OK, ButtonType.CANCEL);
        GridPane pane = new GridPane();
        TextField name = new TextField();
        pane.getChildren().add(name);
        dialog.getDialogPane().setContent(pane);
        dialog.setResultConverter(button -> {
            if (button.getButtonData() == ButtonBar.ButtonData.OK_DONE) {
                recordsFuller.writeRecord(gameMode.toString(),
                        name.getText(),
                        timer.toString());
                dialog.close();
            }
            return null;
        });
        Platform.runLater(dialog::showAndWait);
    }

    public void gameTimer() {
    }

    public boolean isWin() {
        return isWin;
    }

    public int getMinesQuantity() {
        return minesQuantity;
    }

    public void openCell(int x, int y) {
        if (palette.getCell(x, y).isOpen())
            return;

        switch (palette.getCell(x, y).open()) {
            case -1:
                gameOver();
                break;
            case 0:
                palette.openNeighbors(palette.getCell(x, y));
        }
    }

    public void flagCell(int x, int y) {
        if (palette.getCell(x, y).hasFlag()) {
            palette.getCell(x, y).removeFlag();
            bombsCounter.increment();
        } else {
            palette.getCell(x, y).setFlag();
            bombsCounter.decrement();
        }
    }

    public boolean isCellOpen(int x, int y) {
        return palette.getCell(x, y).isOpen();
    }

    public boolean hasCellFlag(int x, int y) {
        return palette.getCell(x, y).hasFlag();
    }

    private void setMinesWeeperButtonsEvents() {
        CellImpl[][] cells = palette.getCells();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                CellImpl cell = cells[x][y];
                Button button = cell.getButton();
                addMinesWeeperButtonClickEvent(button, cell);
                addMinesWeeperButtonDraggedEvents(button);
            }
        }
    }

    private void addMinesWeeperButtonClickEvent(Button button, CellImpl cell) {
        button.setOnMouseClicked(event -> {
            if (!isGameAlive) {
                return;
            }

            if (event.getButton() == MouseButton.PRIMARY) {
                if (isFirstClick) {
                    x_saved = cell.getX();
                    y_saved = cell.getY();
                    if (!timer.isRunning())
                        timer.startTimer();
                    isFirstClick = false;
                    if (cell.hasBomb()) {
                        gameStart(width, height, minesQuantity);
                        game.openCell(x_saved, y_saved);
                    }
                }
                if (!cell.hasFlag())
                    openCell(cell.getX(), cell.getY());
            } else if (event.getButton() == MouseButton.SECONDARY) {
                flagCell(cell.getX(), cell.getY());
                if (cell.hasFlag()) {
                    removeEventsFromButton(button);
                } else {
                    addMinesWeeperButtonDraggedEvents(button);
                }
            }

            if ((width * height - palette.getOpenCellsCount()) == minesQuantity) {
                isWin = true;
                gameOver();
            }
        });
    }

    private void addMinesWeeperButtonDraggedEvents(Button button) {
        button.setOnDragDetected(e -> button.startFullDrag());
        button.setOnMouseDragEntered(e -> button.setGraphic(new ImageView(
                new Image(getClass().getResourceAsStream("/images/pressed.bmp"))))
        );

        button.setOnMouseDragExited(e -> button.setGraphic(
                new ImageView(
                        new Image(getClass().getResourceAsStream("/images/default.bmp")))
                )
        );

        button.setOnMousePressed(event -> {
            if (event.getButton() == MouseButton.PRIMARY)
                smile.setGraphic(new ImageView((new Image(getClass().getResourceAsStream("/images/pressed_smile.bmp")))));
            button.setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/pressed.bmp"))));
        });

        button.setOnMouseReleased(event -> smile.setGraphic(new ImageView(
                new Image(getClass().getResourceAsStream("/images/smile.bmp")))));

        button.setOnMouseExited(event -> button.setGraphic(new ImageView(
                new Image(getClass().getResourceAsStream("/images/default.bmp")))));
    }

    private void removeEventsFromButtons() {
        CellImpl[][] cells = palette.getCells();
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                Button button = cells[x][y].getButton();
                removeEventsFromButton(button);
            }
        }
    }

    private void removeClickEventFromButton(Button button) {
        button.setOnMouseClicked(null);
    }

    private void removeEventsFromButton(Button button) {
        button.setOnDragDetected(null);
        button.setOnMouseDragEntered(null);
        button.setOnMouseDragExited(null);
        button.setOnMousePressed(null);
        button.setOnMouseReleased(null);
        button.setOnMouseExited(null);
        if (!isGameAlive)
            button.setOnMouseClicked(null);
    }

    private void setSmileButtonEvents() {
        smile.setOnMouseClicked(e -> {
            if (timer.isRunning()) {
                timer.stopTimer();
            }
            smile.setGraphic(new ImageView(new Image("/images/smile.bmp")));
            gameStart(width, height, minesQuantity);
        });
    }

    public void getCellNeighbors(int x, int y) {

    }
}


