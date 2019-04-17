package ua.games.minesweeper.bot;

import javafx.application.Platform;
import ua.games.minesweeper.service.CellImpl;
import ua.games.minesweeper.service.Game;
import ua.games.minesweeper.service.GameMode;
import ua.games.minesweeper.service.Palette;
import ua.games.minesweeper.service.Timer;

import java.util.*;
import java.util.stream.Collectors;

public class Bot {
    private Game game = Game.getGame();
    private Palette palette;
    private int height;
    private int width;
    private int minesQuantity;
    private Thread botThread;
    Timer timer;

    CellImpl[][] cells;
    ArrayList<Point> openedCells;
    ArrayList<Point> flaggedCells;
    GameMode gameMode;

    public Bot(GameMode gameMode) {
        this.gameMode = gameMode;
        height = gameMode.getHeight();
        width = gameMode.getWidth();
        minesQuantity = game.getMinesQuantity();
    }

    public void start() {
        openedCells = new ArrayList<>();
        flaggedCells = new ArrayList<>();
        Runnable task = new Runnable() {
            @Override
            public void run() {
                Platform.runLater(Bot.this::run);
            }
        };

        botThread = new Thread(task);
        botThread.setDaemon(true);
        botThread.start();
    }

    private void run() {

        Random random = new Random();
        int rand_height = -1;
        int rand_width = -1;

        game.gameStart(gameMode);

        timer = Timer.getTimer();

        if (timer.isRunning())
            timer.stopTimer();

        timer.startTimer();

        palette = Palette.getPalette();
        cells = palette.getCells();
        do {
            rand_height = random.nextInt(gameMode.getHeight() - 1);
            rand_width = random.nextInt(gameMode.getWidth() - 1);
            try {
                game.openCell(rand_width, rand_height);
            }catch (Exception e){
                run();
            }
        } while (palette.getNeighbors(palette.getCell(rand_width, rand_height))
                .stream().noneMatch(CellImpl::isOpen));

        if (!game.isGameAlive()) {
            run();
        }

        int count = 0;
        for (int y = 0; y < height; y++) {
            for (int x = 0; x < width; x++) {
                if (cells[x][y].isOpen() && (cells[x][y].getNumber() > 0)) {
                    openedCells.add(new Point(cells[x][y].getX(),
                            cells[x][y].getY()));
                    count++;
                }
            }
        }
        checkLeftMines();
    }

    private void checkLeftMines(){
        int count1 = 0;
        while (!(count1 == minesQuantity)) {
            count1 = 0;
            for (int y = 0; y < height; y++) {
                for (int x = 0; x < width; x++) {
                    if (!game.isCellOpen(x, y))
                        count1++;
                }
            }
            int open = workWithCells();
            if(workWithCells() == 0 && !(count1 == minesQuantity)){
                run();
                break;
            } else if(open == 0) {
                break;
            }
        }
    }

    private int workWithCells() {
        flagCellWith100hpBomb();
        return openCellWith100hpBomb();
    }

    private void flagCellWith100hpBomb() {
        for (Point point : openedCells) {
            long closed_neighbors = palette.getNeighbors(
                    palette.getCell(point.getX(), point.getY()))
                    .stream().filter(cell -> !cell.isOpen()).count();
            if (cells[point.getX()][point.getY()].getNumber()
                    == closed_neighbors) {
                for (CellImpl cell :
                        palette.getNeighbors(palette.getCell(point.getX(), point.getY()))) {
                    if (!cell.hasFlag() && !cell.isOpen()) {
                        game.flagCell(cell.getX(), cell.getY());
                        flaggedCells.add(new Point(cell.getX(), cell.getY()));
                    }
                }
            }
        }

    }

    private int openCellWith100hpBomb() {
        int opened = 0;
        ArrayList<Point> buffer = new ArrayList<>();
        for (Point point : openedCells) {
            CellImpl cell = cells[point.getX()][point.getY()];
            long flagged_neighbors =
                    palette.getNeighbors(cell).stream()
                            .filter(CellImpl::hasFlag).count();
            if (flagged_neighbors == cell.getNumber()) {
                for (CellImpl cell1 : palette.getNeighbors(cell)) {
                    if (!game.isCellOpen(cell1.getX(), cell1.getY()) &&
                            !game.hasCellFlag(cell1.getX(), cell1.getY())) {
                        game.openCell(cell1.getX(), cell1.getY());
                        buffer.add(new Point(cell1.getX(), cell1.getY()));
                        opened++;
                    }
                }
            }
        }

        openedCells.addAll(buffer);
        return opened;
    }

    private class Point {
        int x;
        int y;

        Point(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        @Override
        public String toString() {
            return "x: " + x + ", y: " + y;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Point point = (Point) o;
            return x == point.x &&
                    y == point.y;
        }

        @Override
        public int hashCode() {
            return Objects.hash(x, y);
        }
    }
}
