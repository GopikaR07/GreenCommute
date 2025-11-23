package com.greencommute.model;

public class Trip {
    private String mode;
    private int duration;
    private double distance;
    private int points;
    private double fuelSaved;
    private double co2Saved;
    private String icon;

    public Trip(String mode, int duration, double distance, int points, double fuelSaved, double co2Saved, String icon) {
        this.mode = mode;
        this.duration = duration;
        this.distance = distance;
        this.points = points;
        this.fuelSaved = fuelSaved;
        this.co2Saved = co2Saved;
        this.icon = icon;
    }

    // Getters
    public String getMode() { return mode; }
    public int getDuration() { return duration; }
    public double getDistance() { return distance; }
    public int getPoints() { return points; }
    public double getFuelSaved() { return fuelSaved; }
    public double getCo2Saved() { return co2Saved; }
    public String getIcon() { return icon; }
}