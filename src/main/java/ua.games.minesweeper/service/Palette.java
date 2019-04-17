package ua.games.minesweeper.service;

import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.Pane;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Palette extends Pane {

    private int minesQuantity;
    private int width;
    private int height;

    private CellImpl[][] cells;

    private static Palette palette;

    private Palette() {
        super();
        setVisible(true);
    }

    public static Palette getPalette() {
        if (palette == null) {
            palette = new Palette();
        }
        return palette;
    }

    public void render(int width, int height, int mines) {
        this.width = width;
        this.height = height;
        this.minesQuantity = mines;
        cells = new CellImpl[width][height];
        Platform.runLater(() -> {
            getChildren().removeAll(getChildren());
        });
        populateCells();
    }

    private void populateCells() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                CellImpl cell = new CellImpl(x, y);
                cells[x][y] = cell;
                Platform.runLater(() ->
                        getChildren().add(cell));
            }
        }

        addMinesToCell();
        addNumberToCell();
    }

    private void addMinesToCell() {
        for (int i = 0; i < minesQuantity; i++) {
            int rand_x = (int) (Math.random() * width);
            int rand_y = (int) (Math.random() * height);
            CellImpl cell = cells[rand_x][rand_y];
            if (cell.hasBomb()) {
                i--;
            } else {
                cell.setBomb();
            }
        }
    }

    private void addNumberToCell() {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                CellImpl cell = cells[x][y];

                long bombs = getNeighbors(cell).stream().filter(CellImpl::hasBomb).count();
                if (cell.hasBomb())
                    bombs = -1;

                cell.setNumber((int) bombs);
            }
        }
    }

    public int getOpenCellsCount(){
        int openCellsCount = 0;
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                if(cells[x][y].isOpen())
                    openCellsCount++;
            }
        }
        return openCellsCount;
    }

    public int getMinesLeft(){
        int minesLeft = 0;
        for(int y = 0; y < height; y++){
            for(int x = 0; x < width; x++){
                if(cells[x][y].hasBomb())
                    minesLeft++;
            }
        }
        return minesLeft;
    }


    public List<CellImpl> getNeighbors(CellImpl cell) {
        List<CellImpl> neighbors = new ArrayList<>();
        int[] neighborsMap = new int[]{
                -1, -1,
                -1, 0,
                -1, 1,
                0, -1,
                0, 1,
                1, -1,
                1, 0,
                1, 1
        };

        for (int i = 0; i < neighborsMap.length; i++) {
            int dx = neighborsMap[i];
            int dy = neighborsMap[++i];

            int newX = cell.getX() + dx;
            int newY = cell.getY() + dy;

            if (newX >= 0 && newX < width
                    && newY >= 0 && newY < height) {
                neighbors.add(cells[newX][newY]);
            }
        }

        return neighbors;
    }

    public void openNeighbors(CellImpl cell) {
        for (CellImpl c : getNeighbors(cell)) {
            if (!c.isOpen() && c.open() == 0)
                openNeighbors(c);
        }
    }

    public CellImpl getCell(int x, int y) {
        return cells[x][y];
    }

    public CellImpl[][] getCells() {
        return cells;
    }


    public void openAllBombs(boolean isWin) {
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                CellImpl cell = cells[x][y];

                if (cell.hasFlag() && !cell.hasBomb()) {
                    ImageView image = cell.getImage();
                    image.setImage(new Image("/images/flag_without_bomb.bmp"));
                    cell.getChildren().get(1).setVisible(true);
                    cell.getChildren().remove(cell.getChildren().get(0));
                }

                if (!cell.isOpen() && cell.hasBomb() && !cell.hasFlag() && !isWin) {
                    cell.getChildren().get(1).setVisible(true);
                    cell.getChildren().remove(cell.getChildren().get(0));
                }

                if (!cell.isOpen() && cell.hasBomb() && !cell.hasFlag() && isWin) {
                    cell.setFlag();
                }
            }
        }
    }

}
