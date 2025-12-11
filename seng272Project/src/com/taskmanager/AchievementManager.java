package com.taskmanager;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages achievements and tracks progress towards unlocking them.
 */
public class AchievementManager {
    private List<Achievement> achievements;
    private static final String ACHIEVEMENTS_FILE = "C:\\Java\\Yusuf Ali\\seng272Project\\Achievements.txt";
    
    public AchievementManager() {
        achievements = new ArrayList<>();
        initializeDefaultAchievements();
        loadAchievements();
    }
    
    /**
     * Initialize default achievements if none exist.
     */
    private void initializeDefaultAchievements() {
        // Task completion achievements
        achievements.add(new Achievement("TC1", "Task Beginner", "Complete 5 tasks", AchievementType.TASK_COMPLETION, 5));
        achievements.add(new Achievement("TC2", "Task Master", "Complete 20 tasks", AchievementType.TASK_COMPLETION, 20));
        
        // Points achievements
        achievements.add(new Achievement("PE1", "Point Collector", "Earn 50 points", AchievementType.POINTS_EARNED, 50));
        achievements.add(new Achievement("PE2", "Point Hoarder", "Earn 200 points", AchievementType.POINTS_EARNED, 200));
        achievements.add(new Achievement("PE3", "Point Master", "Earn 500 points", AchievementType.POINTS_EARNED, 500));
        
        // Rating achievements
        achievements.add(new Achievement("RE1", "Good Ratings", "Achieve an average rating of 3", AchievementType.RATING_EARNED, 3));
        achievements.add(new Achievement("RE2", "Excellent Ratings", "Achieve an average rating of 5", AchievementType.RATING_EARNED, 5));
        
        // Level achievements
        achievements.add(new Achievement("LR1", "Level Up", "Reach level 2", AchievementType.LEVEL_REACHED, 2));
        achievements.add(new Achievement("LR2", "High Level", "Reach level 3", AchievementType.LEVEL_REACHED, 3));
        achievements.add(new Achievement("LR3", "Max Level", "Reach level 4", AchievementType.LEVEL_REACHED, 4));
        
        // Wish achievements
        achievements.add(new Achievement("WA1", "Wish Granted", "Get a wish approved", AchievementType.WISH_APPROVED, 1));
        achievements.add(new Achievement("WA2", "Dream Achiever", "Get 3 wishes approved", AchievementType.WISH_APPROVED, 3));
    }
    
    /**
     * Load achievements from file.
     */
    private void loadAchievements() {
        try (BufferedReader reader = new BufferedReader(new FileReader(ACHIEVEMENTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    parseAchievement(line);
                }
            }
            System.out.println("Loaded achievements from file.");
        } catch (IOException e) {
            System.err.println("Error loading achievements: " + e.getMessage());
            // Create the file if it doesn't exist
            try {
                new File(ACHIEVEMENTS_FILE).createNewFile();
                System.out.println("Created new Achievements.txt file.");
                saveAchievements(); // Save default achievements
            } catch (IOException ex) {
                System.err.println("Error creating Achievements.txt file: " + ex.getMessage());
            }
        }
    }
    
    /**
     * Parse an achievement from a line in the file.
     * @param line Line to parse
     */
    private void parseAchievement(String line) {
        try {
            String[] parts = line.split(",");
            if (parts.length >= 6) {
                String id = parts[0];
                String name = parts[1];
                String description = parts[2];
                AchievementType type = AchievementType.valueOf(parts[3]);
                int threshold = Integer.parseInt(parts[4]);
                boolean unlocked = Boolean.parseBoolean(parts[5]);
                
                // Check if this achievement already exists
                for (Achievement achievement : achievements) {
                    if (achievement.getId().equals(id)) {
                        achievement.setUnlocked(unlocked);
                        return;
                    }
                }
                
                // If not found, create a new one
                Achievement achievement = new Achievement(id, name, description, type, threshold);
                achievement.setUnlocked(unlocked);
                achievements.add(achievement);
            }
        } catch (Exception e) {
            System.err.println("Error parsing achievement: " + e.getMessage());
        }
    }
    
    /**
     * Save achievements to file.
     */
    private void saveAchievements() {
        try (PrintWriter writer = new PrintWriter(new FileWriter(ACHIEVEMENTS_FILE))) {
            for (Achievement achievement : achievements) {
                writer.println(String.format("%s,%s,%s,%s,%d,%b",
                        achievement.getId(),
                        achievement.getName(),
                        achievement.getDescription(),
                        achievement.getType().name(),
                        achievement.getThreshold(),
                        achievement.isUnlocked()));
            }
            System.out.println("Successfully saved achievements to file.");
        } catch (IOException e) {
            System.err.println("Error saving achievements: " + e.getMessage());
        }
    }
    
    /**
     * Check for task completion achievements.
     * @param completedTasksCount Number of completed tasks
     * @return List of newly unlocked achievements
     */
    public List<Achievement> checkTaskCompletionAchievements(int completedTasksCount) {
        List<Achievement> unlockedAchievements = new ArrayList<>();
        
        for (Achievement achievement : achievements) {
            if (achievement.getType() == AchievementType.TASK_COMPLETION) {
                if (achievement.checkAndUnlock(completedTasksCount)) {
                    unlockedAchievements.add(achievement);
                }
            }
        }
        
        if (!unlockedAchievements.isEmpty()) {
            saveAchievements();
        }
        
        return unlockedAchievements;
    }
    
    /**
     * Check for points earned achievements.
     * @param points Total points earned
     * @return List of newly unlocked achievements
     */
    public List<Achievement> checkPointsAchievements(int points) {
        List<Achievement> unlockedAchievements = new ArrayList<>();
        
        for (Achievement achievement : achievements) {
            if (achievement.getType() == AchievementType.POINTS_EARNED) {
                if (achievement.checkAndUnlock(points)) {
                    unlockedAchievements.add(achievement);
                }
            }
        }
        
        if (!unlockedAchievements.isEmpty()) {
            saveAchievements();
        }
        
        return unlockedAchievements;
    }
    
    /**
     * Check for rating achievements.
     * @param averageRating Average rating earned
     * @return List of newly unlocked achievements
     */
    public List<Achievement> checkRatingAchievements(double averageRating) {
        List<Achievement> unlockedAchievements = new ArrayList<>();
        
        for (Achievement achievement : achievements) {
            if (achievement.getType() == AchievementType.RATING_EARNED) {
                if (achievement.checkAndUnlock((int)Math.round(averageRating))) {
                    unlockedAchievements.add(achievement);
                }
            }
        }
        
        if (!unlockedAchievements.isEmpty()) {
            saveAchievements();
        }
        
        return unlockedAchievements;
    }
    
    /**
     * Check for level achievements.
     * @param level Current level
     * @return List of newly unlocked achievements
     */
    public List<Achievement> checkLevelAchievements(int level) {
        List<Achievement> unlockedAchievements = new ArrayList<>();
        
        for (Achievement achievement : achievements) {
            if (achievement.getType() == AchievementType.LEVEL_REACHED) {
                if (achievement.checkAndUnlock(level)) {
                    unlockedAchievements.add(achievement);
                }
            }
        }
        
        if (!unlockedAchievements.isEmpty()) {
            saveAchievements();
        }
        
        return unlockedAchievements;
    }
    
    /**
     * Check for wish approved achievements.
     * @param approvedWishesCount Number of approved wishes
     * @return List of newly unlocked achievements
     */
    public List<Achievement> checkWishAchievements(int approvedWishesCount) {
        List<Achievement> unlockedAchievements = new ArrayList<>();
        
        for (Achievement achievement : achievements) {
            if (achievement.getType() == AchievementType.WISH_APPROVED) {
                if (achievement.checkAndUnlock(approvedWishesCount)) {
                    unlockedAchievements.add(achievement);
                }
            }
        }
        
        if (!unlockedAchievements.isEmpty()) {
            saveAchievements();
        }
        
        return unlockedAchievements;
    }
    
    /**
     * Get all achievements.
     * @return List of all achievements
     */
    public List<Achievement> getAllAchievements() {
        return new ArrayList<>(achievements);
    }
    
    /**
     * Get all unlocked achievements.
     * @return List of unlocked achievements
     */
    public List<Achievement> getUnlockedAchievements() {
        List<Achievement> unlockedAchievements = new ArrayList<>();
        
        for (Achievement achievement : achievements) {
            if (achievement.isUnlocked()) {
                unlockedAchievements.add(achievement);
            }
        }
        
        return unlockedAchievements;
    }
    
    /**
     * Print all achievements to console.
     */
    public void printAllAchievements() {
        System.out.println("\n===== ACHIEVEMENTS =====");
        
        List<Achievement> unlockedAchievements = getUnlockedAchievements();
        List<Achievement> lockedAchievements = new ArrayList<>();
        
        for (Achievement achievement : achievements) {
            if (!achievement.isUnlocked()) {
                lockedAchievements.add(achievement);
            }
        }
        
        System.out.println("\nUnlocked Achievements (" + unlockedAchievements.size() + "/" + achievements.size() + "):");
        if (unlockedAchievements.isEmpty()) {
            System.out.println("No achievements unlocked yet.");
        } else {
            for (Achievement achievement : unlockedAchievements) {
                System.out.println("- " + achievement);
            }
        }
        
        System.out.println("\nLocked Achievements:");
        if (lockedAchievements.isEmpty()) {
            System.out.println("All achievements unlocked! Congratulations!");
        } else {
            for (Achievement achievement : lockedAchievements) {
                System.out.println("- " + achievement);
            }
        }
        
        System.out.println("=======================");
    }
}
