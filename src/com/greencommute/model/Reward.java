package com.greencommute.model;

public class Reward {
    private int id;
    private String name;
    private String description;
    private int requiredPoints;
    private String imageUrl;
    private String category;

    public Reward(int id, String name, String description, int requiredPoints, String category) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.requiredPoints = requiredPoints;
        this.category = category;
    }

    // Getters and Setters
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public int getRequiredPoints() { return requiredPoints; }
    public void setRequiredPoints(int requiredPoints) { this.requiredPoints = requiredPoints; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }
}
