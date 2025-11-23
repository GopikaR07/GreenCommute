package com.greencommute.model;

public class Challenge {
    private int id;
    private String name;
    private String description;
    private int rewardPoints;
    private boolean completed;
    private int progress;
    private int target;

    public Challenge(int id, String name, String description, int rewardPoints, int target) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.rewardPoints = rewardPoints;
        this.target = target;
        this.completed = false;
        this.progress = 0;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getRewardPoints() { return rewardPoints; }
    public void setRewardPoints(int rewardPoints) { this.rewardPoints = rewardPoints; }

    public boolean isCompleted() { return completed; }
    public void setCompleted(boolean completed) { this.completed = completed; }

    public int getProgress() { return progress; }
    public void setProgress(int progress) { this.progress = progress; }

    public int getTarget() { return target; }
    public void setTarget(int target) { this.target = target; }

    public double getProgressPercentage() {
        return target > 0 ? (progress * 100.0 / target) : 0;
    }
}