package com.taskmanager;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;

/**
 * A system to generate reminders for upcoming tasks and wishes.
 */
public class ReminderSystem {
    private static final DateTimeFormatter DATE_TIME_FORMATTER = 
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    
    /**
     * Get reminders for tasks that are due soon.
     * @param taskManager The task manager containing all tasks
     * @param daysThreshold Number of days to consider as "upcoming"
     * @return List of reminder messages
     */
    public List<String> getTaskReminders(TaskManager taskManager, int daysThreshold) {
        List<String> reminders = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (Task task : taskManager.getAllTasks()) {
            if (task.isCompleted() || task.isApproved()) {
                continue; // Skip completed or approved tasks
            }
            
            LocalDateTime deadline = task.getDeadline();
            if (deadline != null) {
                long daysUntilDue = ChronoUnit.DAYS.between(now, deadline);
                
                if (daysUntilDue <= daysThreshold && daysUntilDue >= 0) {
                    String reminder = String.format("REMINDER: Task '%s' (ID: %s) is due in %d days (on %s).",
                            task.getTitle(), task.getId(), daysUntilDue, 
                            deadline.format(DATE_TIME_FORMATTER));
                    
                    // Add priority information for high priority tasks
                    if (task.getPriority() == TaskPriority.HIGH) {
                        reminder += " This is a HIGH priority task!";
                    }
                    
                    reminders.add(reminder);
                } else if (daysUntilDue < 0) {
                    // Task is overdue
                    String reminder = String.format("ALERT: Task '%s' (ID: %s) is OVERDUE by %d days (was due on %s)!",
                            task.getTitle(), task.getId(), Math.abs(daysUntilDue), 
                            deadline.format(DATE_TIME_FORMATTER));
                    reminders.add(reminder);
                }
            }
        }
        
        return reminders;
    }
    
    /**
     * Get reminders for wishes that are expiring soon.
     * @param wishManager The wish manager containing all wishes
     * @param daysThreshold Number of days to consider as "upcoming expiration"
     * @return List of reminder messages
     */
    public List<String> getWishReminders(WishManager wishManager, int daysThreshold) {
        List<String> reminders = new ArrayList<>();
        LocalDateTime now = LocalDateTime.now();
        
        for (Wish wish : wishManager.getAllWishes()) {
            if (wish.isApproved()) {
                continue; // Skip approved wishes
            }
            
            LocalDateTime endTime = wish.getEndTime();
            if (endTime != null) {
                long daysUntilExpire = ChronoUnit.DAYS.between(now, endTime);
                
                if (daysUntilExpire <= daysThreshold && daysUntilExpire >= 0) {
                    String reminder = String.format("REMINDER: Wish '%s' (ID: %s) will expire in %d days (on %s).",
                            wish.getTitle(), wish.getId(), daysUntilExpire, 
                            endTime.format(DATE_TIME_FORMATTER));
                    reminders.add(reminder);
                }
            }
        }
        
        return reminders;
    }
    
    /**
     * Print all reminders to the console.
     * @param taskManager The task manager
     * @param wishManager The wish manager
     * @param daysThreshold Number of days to consider as "upcoming"
     */
    public void printAllReminders(TaskManager taskManager, WishManager wishManager, int daysThreshold) {
        List<String> taskReminders = getTaskReminders(taskManager, daysThreshold);
        List<String> wishReminders = getWishReminders(wishManager, daysThreshold);
        
        System.out.println("\n===== REMINDERS =====");
        
        if (taskReminders.isEmpty() && wishReminders.isEmpty()) {
            System.out.println("No upcoming reminders.");
            return;
        }
        
        if (!taskReminders.isEmpty()) {
            System.out.println("\nTask Reminders:");
            for (String reminder : taskReminders) {
                System.out.println("- " + reminder);
            }
        }
        
        if (!wishReminders.isEmpty()) {
            System.out.println("\nWish Reminders:");
            for (String reminder : wishReminders) {
                System.out.println("- " + reminder);
            }
        }
        
        System.out.println("=====================");
    }
}
