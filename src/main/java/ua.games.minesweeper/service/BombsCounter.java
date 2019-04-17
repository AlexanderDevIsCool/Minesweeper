package ua.games.minesweeper.service;

import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class BombsCounter extends Label {

    private static BombsCounter bombsCounter;
    private static int bombsLeft;

    private BombsCounter(){
        super();
        setStyle("-fx-background-color: black; -fx-text-fill: red; -fx-font-size: 20;" +
                "-fx-pref-width: 40;");
        setAlignment(Pos.CENTER);
    }

    public static BombsCounter getBombsCounter(int bombsQuantity){
        if(bombsCounter == null)
            bombsCounter = new BombsCounter();
        bombsLeft = bombsQuantity;
        return bombsCounter;
    }

    public int getBombsLeft(){
        return bombsLeft;
    }

    public void decrement(){
        bombsCounter.setText(String.valueOf(--bombsLeft));
    }

    public void increment(){
        bombsCounter.setText(String.valueOf(++bombsLeft));
    }
}
