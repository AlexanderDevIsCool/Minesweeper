package ua.games.minesweeper.service;

import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.Label;

public class Timer extends Label {
    private Thread timerThread;
    private int time;
    private boolean isRunning = false;

    private static Timer timer;

    private Timer() {
        super("000");
        setStyle("-fx-background-color: black; -fx-text-fill: red; -fx-font-size: 20;" +
                "-fx-pref-width: 40;");
        setAlignment(Pos.CENTER);
    }

    public static Timer getTimer() {
        if (timer == null)
            timer = new Timer();
        return timer;
    }

    public boolean isRunning() {
        return (timerThread != null && isRunning);
    }

    public void startTimer() {
        Runnable task = new Runnable() {
            @Override
            public void run() {
                runTask();
            }
        };

        timerThread = new Thread(task);
        timerThread.setDaemon(true);
        timerThread.start();
    }

    private void runTask() {
        isRunning = true;
        for (int i = 0; i < 999; i++) {
            try {
                final String status = String.valueOf(i);
                Platform.runLater(() -> timer.setText(status));
                Thread.sleep(1000);
                time = Integer.parseInt(status);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                Platform.runLater(() -> timer.setText(String.valueOf(time)));
            }
        }
    }

    public void stopTimer() {
    //    time = Integer.parseInt(getText());
        timerThread.interrupt();
    }

    @Override
    public String toString(){
        return String.valueOf(time);
    }
}
