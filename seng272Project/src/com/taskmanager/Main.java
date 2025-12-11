package com.taskmanager;

import java.io.File;
import java.io.FileNotFoundException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Scanner;

public class Main {
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");
    
    private static TaskManager taskManager;
    private static WishManager wishManager;
    private static Child child;
    private static ReminderSystem reminderSystem;
    private static AchievementManager achievementManager;

    public static void main(String[] args) {
        try {
            // Read commands from Commands.txt
            File commandFile = new File("C:\\Java\\Yusuf Ali\\seng272Project\\Commands.txt");
            System.out.println("Reading commands from: " + commandFile.getAbsolutePath());
            Scanner scanner = new Scanner(commandFile);

            // Initialize managers
            taskManager = new TaskManager();
            wishManager = new WishManager();
            child = new Child();
            reminderSystem = new ReminderSystem();
            achievementManager = new AchievementManager();

            // Process commands
            while (scanner.hasNextLine()) {
                String command = scanner.nextLine();
                System.out.println("\nProcessing command: " + command);
                processCommand(command, taskManager, wishManager, child);
            }

            scanner.close();
            System.out.println("\nAll commands processed successfully!");
        } catch (FileNotFoundException e) {
            System.err.println("Error: Commands.txt file not found!");
            e.printStackTrace();
        }
    }

    private static void processCommand(String command, TaskManager taskManager,
                                       WishManager wishManager, Child child) {
        String[] parts = command.split(" ");
        String commandType = parts[0];

        try {
            switch (commandType) {
                case "ADD_TASK1":
                    processAddTask1(parts, taskManager);
                    break;
                case "ADD_TASK2":
                    processAddTask2(parts, taskManager);
                    break;
                case "LIST_ALL_TASKS":
                    processListAllTasks(parts, taskManager);
                    break;
                case "LIST_TASKS_BY_CATEGORY":
                    processListTasksByCategory(parts, taskManager);
                    break;
                case "LIST_TASKS_BY_PRIORITY":
                    processListTasksByPriority(parts, taskManager);
                    break;
                case "LIST_ALL_WISHES":
                    processListAllWishes(wishManager);
                    break;
                case "TASK_DONE":
                    processTaskDone(parts, taskManager);
                    break;
                case "TASK_CHECKED":
                    processTaskChecked(parts, taskManager, child);
                    break;
                case "ADD_WISH1":
                    processAddWish1(parts, wishManager);
                    break;
                case "ADD_WISH2":
                    processAddWish2(parts, wishManager);
                    break;
                case "ADD_BUDGET_COIN":
                    processAddBudgetCoin(parts, child);
                    break;
                case "WISH_CHECKED":
                    processWishChecked(parts, wishManager, child);
                    break;
                case "PRINT_BUDGET":
                    processPrintBudget(child);
                    break;
                case "PRINT_STATUS":
                    processPrintStatus(child);
                    break;
                case "SHOW_REMINDERS":
                    processShowReminders(parts, taskManager, wishManager, reminderSystem);
                    break;
                case "SHOW_ACHIEVEMENTS":
                    processShowAchievements(achievementManager);
                    break;
                case "CHECK_ACHIEVEMENTS":
                    processCheckAchievements(child, achievementManager);
                    break;
                default:
                    System.err.println("Unknown command: " + commandType);
            }
        } catch (Exception e) {
            System.err.println("Error processing command: " + command);
            e.printStackTrace();
        }
    }

    private static void processAddTask1(String[] parts, TaskManager taskManager) {
        // First, reconstruct the command parts properly
        StringBuilder commandBuilder = new StringBuilder();
        for (String part : parts) {
            commandBuilder.append(part).append(" ");
        }
        String fullCommand = commandBuilder.toString().trim();

        // Split the command properly, preserving quoted strings
        String[] properParts = fullCommand.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        String assignedBy = properParts[1];
        String id = properParts[2];
        String title = properParts[3].replace("\"", "");
        String description = properParts[4].replace("\"", "");
        String dateTimeStr = properParts[5] + "T" + properParts[6];
        int points = Integer.parseInt(properParts[7]);

        // Default category and priority
        TaskCategory category = TaskCategory.OTHER;
        TaskPriority priority = TaskPriority.MEDIUM;

        // Check for category and priority in the command
        for (int i = 8; i < properParts.length; i++) {
            if (properParts[i].equals("CATEGORY") && i + 1 < properParts.length) {
                category = TaskCategory.fromString(properParts[i + 1]);
            }
            if (properParts[i].equals("PRIORITY") && i + 1 < properParts.length) {
                priority = TaskPriority.fromString(properParts[i + 1]);
            }
        }

        LocalDateTime deadline = LocalDateTime.parse(dateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Task task = new Task(id, assignedBy, title, description, deadline, points, category, priority);
        taskManager.addTask(task);
        System.out.println("Added new task: " + task);
    }

    private static void processAddTask2(String[] parts, TaskManager taskManager) {
        // First, reconstruct the command parts properly
        StringBuilder commandBuilder = new StringBuilder();
        for (String part : parts) {
            commandBuilder.append(part).append(" ");
        }
        String fullCommand = commandBuilder.toString().trim();

        // Split the command properly, preserving quoted strings
        String[] properParts = fullCommand.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        String assignedBy = properParts[1];
        String id = properParts[2];
        String title = properParts[3].replace("\"", "");
        String description = properParts[4].replace("\"", "");
        String startDateTimeStr = properParts[5] + "T" + properParts[6];
        String endDateTimeStr = properParts[7] + "T" + properParts[8];
        int points = Integer.parseInt(properParts[9]);

        // Default category and priority
        TaskCategory category = TaskCategory.OTHER;
        TaskPriority priority = TaskPriority.MEDIUM;

        // Check for category and priority in the command
        for (int i = 10; i < properParts.length; i++) {
            if (properParts[i].equals("CATEGORY") && i + 1 < properParts.length) {
                category = TaskCategory.fromString(properParts[i + 1]);
            }
            if (properParts[i].equals("PRIORITY") && i + 1 < properParts.length) {
                priority = TaskPriority.fromString(properParts[i + 1]);
            }
        }

        LocalDateTime startTime = LocalDateTime.parse(startDateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime endTime = LocalDateTime.parse(endDateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Task task = new Task(id, assignedBy, title, description, startTime, endTime, points, category, priority);
        taskManager.addTask(task);
        System.out.println("Added new task: " + task);
    }

    private static void processListAllTasks(String[] parts, TaskManager taskManager) {
        if (parts.length > 1) {
            String filter = parts[1];
            if (filter.equals("D")) {
                System.out.println("Daily Tasks:");
            } else if (filter.equals("W")) {
                System.out.println("Weekly Tasks:");
            }
        }
        taskManager.getAllTasks().forEach(System.out::println);
    }

    private static void processListTasksByCategory(String[] parts, TaskManager taskManager) {
        if (parts.length < 2) {
            System.out.println("Please specify a category");
            return;
        }

        TaskCategory category = TaskCategory.fromString(parts[1]);
        List<Task> tasks = taskManager.getTasksByCategory(category);

        System.out.println("\nTasks in category '" + category.getDisplayName() + "':");
        if (tasks.isEmpty()) {
            System.out.println("No tasks found in this category.");
        } else {
            for (Task task : tasks) {
                System.out.println(task);
            }
            System.out.println("Total: " + tasks.size() + " tasks");
        }
    }

    private static void processListTasksByPriority(String[] parts, TaskManager taskManager) {
        if (parts.length < 2) {
            System.out.println("Please specify a priority");
            return;
        }

        TaskPriority priority = TaskPriority.fromString(parts[1]);
        List<Task> tasks = taskManager.getTasksByPriority(priority);

        System.out.println("\nTasks with priority '" + priority.getDisplayName() + "':");
        if (tasks.isEmpty()) {
            System.out.println("No tasks found with this priority.");
        } else {
            for (Task task : tasks) {
                System.out.println(task);
            }
            System.out.println("Total: " + tasks.size() + " tasks");
        }
    }

    private static void processListAllWishes(WishManager wishManager) {
        System.out.println("All Wishes:");
        wishManager.getAllWishes().forEach(System.out::println);
    }

    private static void processTaskDone(String[] parts, TaskManager taskManager) {
        String taskId = parts[1];
        taskManager.markTaskAsCompleted(taskId);
        System.out.println("Marked task " + taskId + " as completed");
    }

    private static void processTaskChecked(String[] parts, TaskManager taskManager, Child child) {
        String taskId = parts[1];
        int rating = Integer.parseInt(parts[2]);
        taskManager.approveTask(taskId, rating);
        child.addRating(rating);
        
        // Add task points to child's budget
        int taskPoints = taskManager.getTaskPoints(taskId);
        child.addPoints(taskPoints);
        
        // Increment completed tasks count
        child.incrementCompletedTasksCount();
        
        System.out.println("Approved task " + taskId + " with rating " + rating + " and added " + taskPoints + " points to budget");
    }

    private static void processAddWish1(String[] parts, WishManager wishManager) {
        // First, reconstruct the command parts properly
        StringBuilder commandBuilder = new StringBuilder();
        for (String part : parts) {
            commandBuilder.append(part).append(" ");
        }
        String fullCommand = commandBuilder.toString().trim();

        // Split the command properly, preserving quoted strings
        String[] properParts = fullCommand.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        String id = properParts[1];
        String title = properParts[2].replace("\"", "");
        String description = properParts[3].replace("\"", "");

        Wish wish = new Wish(id, title, description);
        wishManager.addWish(wish);
        System.out.println("Added new wish: " + wish);
    }

    private static void processAddWish2(String[] parts, WishManager wishManager) {
        // First, reconstruct the command parts properly
        StringBuilder commandBuilder = new StringBuilder();
        for (String part : parts) {
            commandBuilder.append(part).append(" ");
        }
        String fullCommand = commandBuilder.toString().trim();

        // Split the command properly, preserving quoted strings
        String[] properParts = fullCommand.split(" (?=(?:[^\"]*\"[^\"]*\")*[^\"]*$)");

        String id = properParts[1];
        String title = properParts[2].replace("\"", "");
        String description = properParts[3].replace("\"", "");
        String startDateTimeStr = properParts[4] + "T" + properParts[5];
        String endDateTimeStr = properParts[6] + "T" + properParts[7];

        LocalDateTime startTime = LocalDateTime.parse(startDateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        LocalDateTime endTime = LocalDateTime.parse(endDateTimeStr, DateTimeFormatter.ISO_LOCAL_DATE_TIME);
        Wish wish = new Wish(id, title, description, startTime, endTime);
        wishManager.addWish(wish);
        System.out.println("Added new wish: " + wish);
    }

    private static void processAddBudgetCoin(String[] parts, Child child) {
        int points = Integer.parseInt(parts[1]);
        child.addPoints(points);
        System.out.println("Added " + points + " points to child's budget");
    }

    private static void processWishChecked(String[] parts, WishManager wishManager, Child child) {
        String wishId = parts[1];
        String status = parts[2];

        if (status.equals("APPROVED")) {
            int requiredLevel = Integer.parseInt(parts[3]);
            wishManager.approveWish(wishId, requiredLevel);
            
            // Increment approved wishes count
            child.incrementApprovedWishesCount();
            
            System.out.println("Approved wish " + wishId + " with required level " + requiredLevel);
        } else if (status.equals("REJECTED")) {
            wishManager.rejectWish(wishId);
            System.out.println("Rejected wish " + wishId);
        }
    }

    private static void processPrintBudget(Child child) {
        System.out.println("Current Budget: " + child.getPoints() + " points");
    }

    private static void processPrintStatus(Child child) {
        System.out.println(child);
    }
    
    private static void processShowReminders(String[] parts, TaskManager taskManager, 
                                          WishManager wishManager, ReminderSystem reminderSystem) {
        int daysThreshold = 7; // Default threshold is 7 days
        
        // Check if a custom threshold was provided
        if (parts.length > 1) {
            try {
                daysThreshold = Integer.parseInt(parts[1]);
            } catch (NumberFormatException e) {
                System.out.println("Invalid days threshold, using default of 7 days.");
            }
        }
        
        reminderSystem.printAllReminders(taskManager, wishManager, daysThreshold);
    }
    
    private static void processShowAchievements(AchievementManager achievementManager) {
        achievementManager.printAllAchievements();
    }
    
    private static void processCheckAchievements(Child child, AchievementManager achievementManager) {
        List<Achievement> newlyUnlocked = child.checkAchievements(achievementManager);
        
        if (newlyUnlocked.isEmpty()) {
            System.out.println("No new achievements unlocked.");
        } else {
            System.out.println("\n*** CONGRATULATIONS! ***");
            System.out.println("You've unlocked " + newlyUnlocked.size() + " new achievement(s):");
            for (Achievement achievement : newlyUnlocked) {
                System.out.println("- " + achievement.getName() + ": " + achievement.getDescription());
            }
        }
    }
}