package com.taskmanager;

import java.time.LocalDateTime;

public class Wish {
    private String id;
    private String title;
    private String description;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private boolean isApproved;
    private int requiredLevel;

    public Wish(String id, String title, String description) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.isApproved = false;
        this.requiredLevel = 0;
    }

    public Wish(String id, String title, String description,
                LocalDateTime startTime, LocalDateTime endTime) {
        this(id, title, description);
        this.startTime = startTime;
        this.endTime = endTime;
    }

    // Getters and Setters
    public String getId() { return id; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public LocalDateTime getStartTime() { return startTime; }
    public LocalDateTime getEndTime() { return endTime; }
    public boolean isApproved() { return isApproved; }
    public int getRequiredLevel() { return requiredLevel; }

    public void setApproved(boolean approved) { this.isApproved = approved; }
    public void setRequiredLevel(int level) { this.requiredLevel = level; }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("Wish[ID: ").append(id)
          .append(", Title: ").append(title)
          .append(", Status: ").append(isApproved ? "Approved" : "Pending");
        
        if (isApproved) {
            sb.append(", Required Level: ").append(requiredLevel);
        }
        
        if (startTime != null) {
            sb.append(", Time: ").append(startTime).append(" to ").append(endTime);
        }
        
        sb.append("]");
        return sb.toString();
    }
}