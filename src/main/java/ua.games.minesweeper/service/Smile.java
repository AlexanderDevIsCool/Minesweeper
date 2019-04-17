package ua.games.minesweeper.service;

import javafx.scene.control.Button;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.text.Font;

public class Smile extends Button {
    private static Smile smile;

    private Smile(){
        super();
        setFont(Font.font(0));
        setPrefSize(30, 30);
        setStyle("-fx-background-color:transparent;-fx-padding:0;-fx-background-size:0;");
        setGraphic(new ImageView(new Image("/images/smile.bmp")));
    }

    public static Smile getSmile(){
        if(smile == null)
            smile = new Smile();
        return smile;
    }
}
