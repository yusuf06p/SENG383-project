package com.taskmanager;

import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class TaskManager {
    private List<Task> tasks;
    private static final String TASKS_FILE = "C:\\Java\\Yusuf Ali\\seng272Project\\Task.txt";  // Using absolute path
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter TIME_FORMATTER = DateTimeFormatter.ofPattern("HH:mm");

    public TaskManager() {
        tasks = new ArrayList<>();
        System.out.println("TaskManager initialized. Tasks file path: " + TASKS_FILE);
        loadTasks();
    }

    private void loadTasks() {
        try (BufferedReader reader = new BufferedReader(new FileReader(TASKS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    Task task = parseTask(line);
                    if (task != null) {
                        tasks.add(task);
                    }
                }
            }
            System.out.println("Loaded " + tasks.size() + " tasks from file.");
        } catch (IOException e) {
            System.err.println("Error loading tasks: " + e.getMessage());
            // Create the file if it doesn't exist
            try {
                new File(TASKS_FILE).createNewFile();
                System.out.println("Created new Tasks.txt file.");
            } catch (IOException ex) {
                System.err.println("Error creating Tasks.txt file: " + ex.getMessage());
            }
        }
    }

    private Task parseTask(String line) {
        try {
            String[] parts = line.split(" ");
            String type = parts[0];
            String assignedBy = parts[1];
            String id = parts[2];
            String title = parts[3].replace("\"", "");
            String description = parts[4].replace("\"", "");

            // Check if category and priority are specified
            TaskCategory category = TaskCategory.OTHER;
            TaskPriority priority = TaskPriority.MEDIUM;

            // Parse points which is always the last element
            int points = Integer.parseInt(parts[parts.length - 1]);

            // Check if there are category and priority fields
            for (int i = 0; i < parts.length; i++) {
                if (parts[i].equals("CATEGORY") && i + 1 < parts.length) {
                    category = TaskCategory.fromString(parts[i + 1]);
                }
                if (parts[i].equals("PRIORITY") && i + 1 < parts.length) {
                    priority = TaskPriority.fromString(parts[i + 1]);
                }
            }

            if (type.equals("TASK1")) {
                LocalDateTime deadline = LocalDateTime.parse(
                        parts[5] + "T" + parts[6],
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                );
                return new Task(id, assignedBy, title, description, deadline, points, category, priority);
            } else if (type.equals("TASK2")) {
                LocalDateTime startTime = LocalDateTime.parse(
                        parts[5] + "T" + parts[6],
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                );
                LocalDateTime endTime = LocalDateTime.parse(
                        parts[7] + "T" + parts[8],
                        DateTimeFormatter.ISO_LOCAL_DATE_TIME
                );
                return new Task(id, assignedBy, title, description, startTime, endTime, points, category, priority);
            }
        } catch (Exception e) {
            System.err.println("Error parsing task: " + e.getMessage());
        }
        return null;
    }

    public void addTask(Task task) {
        System.out.println("Adding new task: " + task);
        tasks.add(task);
        System.out.println("Current task count: " + tasks.size());
        saveTasks();
    }

    public void markTaskAsCompleted(String taskId) {
        for (Task task : tasks) {
            if (task.getId().equals(taskId)) {
                task.setCompleted(true);
                saveTasks();
                return;
            }
        }
    }

    public void approveTask(String taskId, int rating) {
        for (Task task : tasks) {
            if (task.getId().equals(taskId)) {
                task.setApproved(true);
                task.setRating(rating);
                saveTasks();
                return;
            }
        }
    }

    public List<Task> getAllTasks() {
        return new ArrayList<>(tasks);
    }

    public List<Task> getTasksByCategory(TaskCategory category) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getCategory() == category) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    public List<Task> getTasksByPriority(TaskPriority priority) {
        List<Task> filteredTasks = new ArrayList<>();
        for (Task task : tasks) {
            if (task.getPriority() == priority) {
                filteredTasks.add(task);
            }
        }
        return filteredTasks;
    }

    public int getTaskPoints(String taskId) {
        for (Task task : tasks) {
            if (task.getId().equals(taskId)) {
                return task.getPoints();
            }
        }
        return 0;
    }

    private void saveTasks() {
        System.out.println("Attempting to save tasks to: " + TASKS_FILE);
        try (PrintWriter writer = new PrintWriter(new FileWriter(TASKS_FILE))) {
            System.out.println("Successfully opened file for writing");
            for (Task task : tasks) {
                String taskString = taskToString(task);
                writer.println(taskString);
                System.out.println("Writing task to file: " + taskString);
            }
            writer.flush();
            System.out.println("Successfully saved " + tasks.size() + " tasks to file.");
        } catch (IOException e) {
            System.err.println("Error saving tasks: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private String taskToString(Task task) {
        StringBuilder sb = new StringBuilder();
        sb.append(task.getStartTime() != null ? "TASK2" : "TASK1")
                .append(" ").append(task.getAssignedBy())
                .append(" ").append(task.getId())
                .append(" \"").append(task.getTitle()).append("\"")
                .append(" \"").append(task.getDescription()).append("\"");

        if (task.getStartTime() != null) {
            sb.append(" ").append(task.getStartTime().format(DATE_FORMATTER))
                    .append(" ").append(task.getStartTime().format(TIME_FORMATTER))
                    .append(" ").append(task.getEndTime().format(DATE_FORMATTER))
                    .append(" ").append(task.getEndTime().format(TIME_FORMATTER));
        } else {
            sb.append(" ").append(task.getDeadline().format(DATE_FORMATTER))
                    .append(" ").append(task.getDeadline().format(TIME_FORMATTER));
        }

        // Add category and priority information
        sb.append(" CATEGORY ").append(task.getCategory().name())
          .append(" PRIORITY ").append(task.getPriority().name())
          .append(" POINT ").append(task.getPoints());
        return sb.toString();
    }
}