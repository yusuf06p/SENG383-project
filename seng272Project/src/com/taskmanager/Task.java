package com.taskmanager;

import java.time.LocalDateTime;

public class Task {
    private String id;
    private String assignedBy; // T for Teacher, F for Parent
    private String title;
    private String description;
    private LocalDateTime deadline;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int points;
    private boolean isCompleted;
    private boolean isApproved;
    private int rating;
    private TaskCategory category;
    private TaskPriority priority;

    public Task(String id, String assignedBy, String title, String description,
                LocalDateTime deadline, int points) {
        this.id = id;
        this.assignedBy = assignedBy;
        this.title = title;
        this.description = description;
        this.deadline = deadline;
        this.points = points;
        this.isCompleted = false;
        this.isApproved = false;
        this.rating = 0;
        this.category = TaskCategory.OTHER;
        this.priority = TaskPriority.MEDIUM;
    }
    
    public Task(String id, String assignedBy, String title, String description,
                LocalDateTime deadline, int points, TaskCategory category, TaskPriority priority) {
        this(id, assignedBy, title, description, deadline, points);
        this.category = category;
        this.priority = priority;
    }

    public Task(String id, String assignedBy, String title, String description,
                LocalDateTime startTime, LocalDateTime endTime, int points) {
        this.id = id;
        this.assignedBy = assignedBy;
        this.title = title;
        this.description = description;
        this.startTime = startTime;
        this.endTime = endTime;
        this.points = points;
        this.isCompleted = false;
        this.isApproved = false;
        this.rating = 0;
        this.category = TaskCategory.OTHER;
        this.priority = TaskPriority.MEDIUM;
    }
    
    public Task(String id, String assignedBy, String title, String description,
                LocalDateTime startTime, LocalDateTime endTime, int points, 
                TaskCategory category, TaskPriority priority) {
        this(id, assignedBy, title, description, startTime, endTime, points);
        this.category = category;
        this.priority = priority;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getAssignedBy() { return assignedBy; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getDeadline() { return deadline; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public int getPoints() { return points; }
    public boolean isCompleted() { return isCompleted; }
    public boolean isApproved() { return isApproved; }
    public int getRating() { return rating; }
    public TaskCategory getCategory() { return category; }
    public TaskPriority getPriority() { return priority; }

    public void setCompleted(boolean completed) { this.isCompleted = completed; }
    public void setApproved(boolean approved) { this.isApproved = approved; }
    public void setRating(int rating) { this.rating = rating; }
    public void setCategory(TaskCategory category) { this.category = category; }
    public void setPriority(TaskPriority priority) { this.priority = priority; }

    @Override
    public String toString() {
        return String.format("Task[ID: %s, Title: %s, Category: %s, Priority: %s, Assigned by: %s, Points: %d, Status: %s]",
                id, title, category.getDisplayName(), priority.getDisplayName(), assignedBy, points, 
                isCompleted ? "Completed" : "Pending");
    }
}