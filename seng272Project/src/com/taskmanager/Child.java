package com.taskmanager;

import java.util.ArrayList;
import java.util.List;

public class Child {
    private int points;
    private List<Integer> ratings;
    private int completedTasksCount;
    private int approvedWishesCount;

    public Child() {
        this.points = 0;
        this.ratings = new ArrayList<>();
        this.completedTasksCount = 0;
        this.approvedWishesCount = 0;
        System.out.println("Child initialized with 0 points");
    }

    public void addPoints(int points) {
        this.points += points;
        System.out.println("Added " + points + " points to budget. Total points: " + this.points);
    }
    
    public void incrementCompletedTasksCount() {
        this.completedTasksCount++;
    }
    
    public void incrementApprovedWishesCount() {
        this.approvedWishesCount++;
    }

    public void addRating(int rating) {
        ratings.add(rating);
        System.out.println("Added rating " + rating + ". Total ratings: " + ratings.size());
    }

    public int getPoints() {
        return points;
    }
    
    public int getCompletedTasksCount() {
        return completedTasksCount;
    }
    
    public int getApprovedWishesCount() {
        return approvedWishesCount;
    }

    public int getLevel() {
        if (points >= 100) return 4;
        if (points >= 75) return 3;
        if (points >= 50) return 2;
        return 1;
    }

    private double calculateAverageRating() {
        if (ratings.isEmpty()) return 0;
        return ratings.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    @Override
    public String toString() {
        return String.format("Child[Points: %d, Level: %d, Average Rating: %.2f, Completed Tasks: %d, Approved Wishes: %d]",
                points, getLevel(), calculateAverageRating(), completedTasksCount, approvedWishesCount);
    }
    
    /**
     * Check for achievements based on the child's current status.
     * @param achievementManager The achievement manager to check against
     * @return List of newly unlocked achievements
     */
    public List<Achievement> checkAchievements(AchievementManager achievementManager) {
        List<Achievement> newlyUnlocked = new ArrayList<>();
        
        // Check each type of achievement
        newlyUnlocked.addAll(achievementManager.checkTaskCompletionAchievements(completedTasksCount));
        newlyUnlocked.addAll(achievementManager.checkPointsAchievements(points));
        newlyUnlocked.addAll(achievementManager.checkRatingAchievements(calculateAverageRating()));
        newlyUnlocked.addAll(achievementManager.checkLevelAchievements(getLevel()));
        newlyUnlocked.addAll(achievementManager.checkWishAchievements(approvedWishesCount));
        
        return newlyUnlocked;
    }
}