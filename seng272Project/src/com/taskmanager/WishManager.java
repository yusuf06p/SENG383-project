package com.taskmanager;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class WishManager {
    private List<Wish> wishes;
    private static final String WISHES_FILE = "C:\\Java\\Yusuf Ali\\seng272Project\\Wish.txt";  // Using absolute path
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public WishManager() {
        wishes = new ArrayList<>();
        System.out.println("WishManager initialized. Wishes file path: " + WISHES_FILE);
        loadWishes();
    }

    private void loadWishes() {
        try (BufferedReader reader = new BufferedReader(new FileReader(WISHES_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Wish wish = parseWish(line);
                    if (wish != null) {
                        wishes.add(wish);
                    }
                }
            }
            System.out.println("Loaded " + wishes.size() + " wishes from file.");
        } catch (IOException e) {
            System.err.println("Error loading wishes: " + e.getMessage());
            // Create the file if it doesn't exist
            try {
                new File(WISHES_FILE).createNewFile();
                System.out.println("Created new Wishes.txt file.");
            } catch (IOException ex) {
                System.err.println("Error creating Wishes.txt file: " + ex.getMessage());
            }
        }
    }

    private Wish parseWish(String line) {
        try {
            // Split the line properly, preserving quoted strings
            String[] parts = line.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");
            
            String type = parts[0];
            String id = parts[1];
            String title = parts[2].replace("\"", "");
            String description = parts[3].replace("\"", "");

            if (type.equals("WISH1")) {
                return new Wish(id, title, description);
            } else if (type.equals("WISH2")) {
                LocalDateTime startTime = LocalDateTime.parse(
                        parts[4] + "T" + parts[5],
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                );
                LocalDateTime endTime = LocalDateTime.parse(
                        parts[6] + "T" + parts[7],
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                );
                return new Wish(id, title, description, startTime, endTime);
            }
        } catch (Exception e) {
            System.err.println("Error parsing wish: " + e.getMessage());
        }
        return null;
    }

    public void addWish(Wish wish) {
        wishes.add(wish);
        saveWishes();
    }

    public void approveWish(String wishId, int requiredLevel) {
        for (Wish wish : wishes) {
            if (wish.getId().equals(wishId)) {
                wish.setApproved(true);
                wish.setRequiredLevel(requiredLevel);
                saveWishes();
                return;
            }
        }
    }

    public void rejectWish(String wishId) {
        wishes.removeIf(wish -> wish.getId().equals(wishId));
        saveWishes();
    }

    public List<Wish> getAllWishes() {
        return new ArrayList<>(wishes);
    }

    private void saveWishes() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(WISHES_FILE))) {
            for (Wish wish : wishes) {
                String wishString = wishToString(wish);
                writer.println(wishString);
                System.out.println("Writing wish to file: " + wishString);
            }
            System.out.println("Successfully saved " + wishes.size() + " wishes to file.");
        } catch (IOException e) {
            System.err.println("Error saving wishes: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String wishToString(Wish wish) {
        StringBuilder sb = new StringBuilder();
        sb.append(wish.getStartTime() != null ? "WISH2" : "WISH1")
                .append(" ").append(wish.getId())
                .append(" \"").append(wish.getTitle()).append("\"")
                .append(" \"").append(wish.getDescription()).append("\"");

        if (wish.getStartTime() != null) {
            sb.append(" ").append(wish.getStartTime().format(DATE_FORMATTER))
                    .append(" ").append(wish.getStartTime().format(TIME_FORMATTER))
                    .append(" ").append(wish.getEndTime().format(DATE_FORMATTER))
                    .append(" ").append(wish.getEndTime().format(TIME_FORMATTER));
        }

        if (wish.isApproved()) {
            sb.append(" LEVEL ").append(wish.getRequiredLevel());
        }

        return sb.toString();
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (Wish wish : wishes) {
            sb.append(wishToString(wish));
            if (wish.isApproved()) {
                sb.append(" LEVEL ").append(wish.getRequiredLevel());
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}