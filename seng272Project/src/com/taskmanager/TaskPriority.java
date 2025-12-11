package com.taskmanager;

/**
 * Represents priority levels for tasks to help with sorting and urgency indication.
 */
public enum TaskPriority {
    HIGH("High", 3),
    MEDIUM("Medium", 2),
    LOW("Low", 1);

    private final String displayName;
    private final int value;

    TaskPriority(String displayName, int value) {
        this.displayName = displayName;
        this.value = value;
    }

    public String getDisplayName() {
        return displayName;
    }

    public int getValue() {
        return value;
    }

    /**
     * Convert a string to a TaskPriority enum value.
     * @param priorityStr The string representation of the priority
     * @return The corresponding TaskPriority or MEDIUM if not found
     */
    public static TaskPriority fromString(String priorityStr) {
        if (priorityStr == null || priorityStr.isEmpty()) {
            return MEDIUM;
        }

        for (TaskPriority priority : TaskPriority.values()) {
            if (priority.name().equalsIgnoreCase(priorityStr) || 
                priority.getDisplayName().equalsIgnoreCase(priorityStr)) {
                return priority;
            }
        }
        return MEDIUM;
    }
}
