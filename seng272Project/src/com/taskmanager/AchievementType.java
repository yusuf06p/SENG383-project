package com.taskmanager;

/**
 * Represents different types of achievements that can be earned.
 */
public enum AchievementType {
    TASK_COMPLETION("Task Completion"),
    POINTS_EARNED("Points Earned"),
    RATING_EARNED("Rating Earned"),
    LEVEL_REACHED("Level Reached"),
    WISH_APPROVED("Wish Approved");
    
    private final String displayName;
    
    AchievementType(String displayName) {
        this.displayName = displayName;
    }
    
    public String getDisplayName() {
        return displayName;
    }
}
