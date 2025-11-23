package com.greencommute.model;

import java.time.LocalDate;

public class Commute {
    private int id;
    private int userId;
    private LocalDate date;
    private String fromLocation;
    private String toLocation;
    private String mode;
    private int duration;
    private double distance;
    private double co2Saved;
    private int pointsEarned;

    public Commute() {}

    public Commute(LocalDate date, String from, String to, String mode, int duration) {
        this.date = date;
        this.fromLocation = from;
        this.toLocation = to;
        this.mode = mode;
        this.duration = duration;
    }

    public Commute(int id, int userId, LocalDate date, String from, String to, String mode,
                   int duration, double distance, double co2Saved, int pointsEarned) {
        this.id = id;
        this.userId = userId;
        this.date = date;
        this.fromLocation = from;
        this.toLocation = to;
        this.mode = mode;
        this.duration = duration;
        this.distance = distance;
        this.co2Saved = co2Saved;
        this.pointsEarned = pointsEarned;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public LocalDate getDate() { return date; }
    public void setDate(LocalDate date) { this.date = date; }

    public String getFromLocation() { return fromLocation; }
    public void setFromLocation(String fromLocation) { this.fromLocation = fromLocation; }

    public String getToLocation() { return toLocation; }
    public void setToLocation(String toLocation) { this.toLocation = toLocation; }

    public String getMode() { return mode; }
    public void setMode(String mode) { this.mode = mode; }

    public int getDuration() { return duration; }
    public void setDuration(int duration) { this.duration = duration; }

    public double getDistance() { return distance; }
    public void setDistance(double distance) { this.distance = distance; }

    public double getCo2Saved() { return co2Saved; }
    public void setCo2Saved(double co2Saved) { this.co2Saved = co2Saved; }

    public int getPointsEarned() { return pointsEarned; }
    public void setPointsEarned(int pointsEarned) { this.pointsEarned = pointsEarned; }
}
