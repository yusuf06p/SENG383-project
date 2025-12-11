package com.taskmanager;

/**
 * Represents categories for tasks to help with organization and filtering.
 */
public enum TaskCategory {
    HOMEWORK("Homework"),
    CHORE("Chore"),
    STUDY("Study"),
    READING("Reading"),
    EXERCISE("Exercise"),
    OTHER("Other");

    private final String displayName;

    TaskCategory(String displayName) {
        this.displayName = displayName;
    }

    public String getDisplayName() {
        return displayName;
    }

    /**
     * Convert a string to a TaskCategory enum value.
     * @param categoryStr The string representation of the category
     * @return The corresponding TaskCategory or OTHER if not found
     */
    public static TaskCategory fromString(String categoryStr) {
        if (categoryStr == null || categoryStr.isEmpty()) {
            return OTHER;
        }

        for (TaskCategory category : TaskCategory.values()) {
            if (category.name().equalsIgnoreCase(categoryStr) || 
                category.getDisplayName().equalsIgnoreCase(categoryStr)) {
                return category;
            }
        }
        return OTHER;
    }
}
