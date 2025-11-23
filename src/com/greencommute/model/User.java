package com.greencommute.model;

public class User {
    private int id;
    private String fullname;
    private String email;
    private int currentStreak;
    private int highestStreak;
    private int totalPoints;
    private String rewardTier; // "Bronze", "Silver", "Gold", "Platinum"

    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getFullname() { return fullname; }
    public void setFullname(String fullname) { this.fullname = fullname; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getCurrentStreak() { return currentStreak; }
    public void setCurrentStreak(int streak) { this.currentStreak = streak; }

    public int getHighestStreak() { return highestStreak; }
    public void setHighestStreak(int highest) { this.highestStreak = highest; }

    public int getTotalPoints() { return totalPoints; }
    public void setTotalPoints(int totalPoints) { this.totalPoints = totalPoints; }

    public String getRewardTier() { return rewardTier; }
    public void setRewardTier(String rewardTier) { this.rewardTier = rewardTier; }
}