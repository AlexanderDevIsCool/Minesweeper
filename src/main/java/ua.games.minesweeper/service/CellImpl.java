package ua.games.minesweeper.service;

import javafx.application.Platform;
import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.StackPane;
import javafx.scene.text.Font;

public class CellImpl extends StackPane {

    private CellButton button = new CellButton();
    private CellImage image;

    private static final int CELL_SIZE = 20;

    private int x;
    private int y;

    private boolean hasBomb;
    private boolean isOpen = false;
    private boolean hasFlag = false;

    private int number;

    CellImpl(int x, int y) {
        this.x = x;
        this.y = y;

        setPrefSize(20, 20);

        setTranslateX(x * CELL_SIZE);
        setTranslateY(y * CELL_SIZE);

    }

    void setBomb() {
        hasBomb = true;
    }

    public int open() {
        if (hasBomb) {
            image.setImage(new Image("/images/bomb_pressed.bmp"));
            return -1;
        }

        isOpen = true;
        image.setVisible(true);
        Platform.runLater(() -> {
            getChildren().remove(button);
        });
        if (number > 0)
            return 1;
        return 0;
    }

    public boolean hasBomb() {
        return hasBomb;
    }

    public boolean isOpen() {
        return isOpen;
    }

    public boolean hasFlag() {
        return hasFlag;
    }

    public void setFlag() {
        button.setGraphic(new ImageView(new Image(
                getClass().getResourceAsStream("/images/flag.bmp")
        )));
        hasFlag = true;
    }

    public void removeFlag() {
        button.setGraphic(new ImageView(new Image(
                getClass().getResourceAsStream("/images/default.bmp")
        )));
        hasFlag = false;
    }

    public int getNumber() {
        return number;
    }

    public void setNumber(int number) {
        this.number = number;
        image = new CellImage(number);
        addChildren();
    }

    public void addChildren() {
        Platform.runLater(() -> {
            getChildren().addAll(button, image);
        });
        image.setVisible(false);
        button.setVisible(true);
    }

    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    public CellButton getButton() {
        return button;
    }

    public CellImage getImage() {
        return image;
    }

    private class CellImage extends ImageView {
        CellImage(int number) {
            super();
            setFitHeight(CELL_SIZE);
            setFitWidth(CELL_SIZE);
            setVisible(false);

            choosePicture(number);
        }

        private void choosePicture(int number) {
            switch (number) {
                case -1:
                    setImage(new Image("/images/bomb.bmp"));
                    break;
                case 0:
                    setImage(new Image("/images/background.bmp"));
                    break;
                case 1:
                    setImage(new Image("/images/one.bmp"));
                    break;
                case 2:
                    setImage(new Image("/images/two.bmp"));
                    break;
                case 3:
                    setImage(new Image("/images/three.bmp"));
                    break;
                case 4:
                    setImage(new Image("/images/four.bmp"));
                    break;
                case 5:
                    setImage(new Image("/images/five.bmp"));
                    break;
                case 6:
                    setImage(new Image("/images/six.bmp"));
                    break;
                case 7:
                    setImage(new Image("/images/seven.bmp"));
                    break;
            }
        }
    }

    private class CellButton extends Button {
        CellButton() {
            super();
            setFont(Font.font(0));
            setMaxSize(CELL_SIZE, CELL_SIZE);
            setGraphic(new ImageView(new Image(getClass().getResourceAsStream("/images/default.bmp"))));
            setVisible(true);
        }
    }
}
