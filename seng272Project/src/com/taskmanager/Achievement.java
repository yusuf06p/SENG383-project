package com.taskmanager;

/**
 * Represents an achievement that can be earned by a child.
 */
public class Achievement {
    private String id;
    private String name;
    private String description;
    private AchievementType type;
    private int threshold;
    private boolean unlocked;
    
    /**
     * Create a new achievement.
     * 
     * @param id Unique identifier for the achievement
     * @param name Name of the achievement
     * @param description Description of how to earn the achievement
     * @param type Type of achievement
     * @param threshold Value needed to unlock the achievement
     */
    public Achievement(String id, String name, String description, AchievementType type, int threshold) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.type = type;
        this.threshold = threshold;
        this.unlocked = false;
    }
    
    /**
     * Check if the achievement should be unlocked based on the provided value.
     * 
     * @param value The current value to check against the threshold
     * @return true if the achievement was just unlocked, false otherwise
     */
    public boolean checkAndUnlock(int value) {
        if (!unlocked && value >= threshold) {
            unlocked = true;
            return true;
        }
        return false;
    }
    
    // Getters
    public String getId() { return id; }
    public String getName() { return name; }
    public String getDescription() { return description; }
    public AchievementType getType() { return type; }
    public int getThreshold() { return threshold; }
    public boolean isUnlocked() { return unlocked; }
    
    // Setter for unlocked status (for loading from file)
    public void setUnlocked(boolean unlocked) { this.unlocked = unlocked; }
    
    @Override
    public String toString() {
        return String.format("%s: %s - %s [%s]", 
                name, description, unlocked ? "Unlocked" : "Locked", type.getDisplayName());
    }
}
