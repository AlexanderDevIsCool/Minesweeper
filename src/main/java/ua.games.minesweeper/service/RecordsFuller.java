package ua.games.minesweeper.service;

import java.io.*;
import java.nio.file.Files;
import java.util.*;

public class RecordsFuller {

    private File file;

    public RecordsFuller() {
        createIfNotExists();
    }

    private void createIfNotExists() {
        file = new File("src/main/resources/data/records.txt");
        try {
            if (file.createNewFile()) {
                FileWriter fileWriter = new FileWriter(file);
                fileWriter.write("Beginner anonymous 999\n");
                fileWriter.write("Intermediate anonymous 999\n");
                fileWriter.write("Expert anonymous 999");
                System.out.println("write");
                fileWriter.flush();
                fileWriter.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void writeRecord(String gameMode, String name, String time) {
        createIfNotExists();
        try {
            StringBuilder builder = new StringBuilder();
            List<String> records = new ArrayList<>(readRecords());
            switch (gameMode) {
                case "Beginner":
                    records.set(0, builder.append("Beginner ")
                            .append(name).append(" ").append(time).toString());
                    break;
                case "Intermediate":
                    records.set(1, builder.append("Intermediate ")
                            .append(name).append(" ").append(time).toString());
                    break;
                case "Expert":
                    records.set(2, builder.append("Expert ")
                            .append(name).append(" ").append(time).toString());
                    break;
            }
            Files.write(file.toPath(), records);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public List<String> readRecords() {
        List<String> records = Collections.emptyList();
        if (file.length() < 1)
            return records;

        try {
            records = Files.readAllLines(file.toPath());
        } catch (IOException e) {
            e.printStackTrace();
        }

        return records;
    }

    public int getRecord(GameMode gameMode){
        List<String> records = readRecords();
        int record = 0;
        switch (gameMode.toString()){
            case "Beginner":
                record = Integer.parseInt(records.get(0).split(" ")[2]);
                break;
            case "Intermediate":
                record = Integer.parseInt(records.get(1).split(" ")[2]);
                break;
            case "Expert":
                record = Integer.parseInt(records.get(2).split(" ")[2]);
                break;
        }
        return record;
    }

    public String getName(GameMode gameMode){
        List<String> records = readRecords();
        String name = "";
        switch (gameMode.toString()){
            case "Beginner":
                name = records.get(0).split(" ")[1];
                break;
            case "Intermediate":
               name = records.get(1).split(" ")[1];
                break;
            case "Expert":
                name = records.get(2).split(" ")[1];
                break;
        }
        return name;
    }

    public void resetBestRecords(){
        try {
            Files.write(file.toPath(),
                    Collections.singletonList("Beginner anonymoues 999\nIntermediate anonymous 999\nExpert anonymous 999"));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
